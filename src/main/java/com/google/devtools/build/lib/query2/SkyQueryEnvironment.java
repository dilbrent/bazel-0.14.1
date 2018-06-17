// Copyright 2015 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.google.devtools.build.lib.query2;

import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;

import com.google.common.base.Ascii;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Throwables;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import com.google.common.util.concurrent.AsyncCallable;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.devtools.build.lib.cmdline.Label;
import com.google.devtools.build.lib.cmdline.PackageIdentifier;
import com.google.devtools.build.lib.cmdline.TargetParsingException;
import com.google.devtools.build.lib.cmdline.TargetPattern;
import com.google.devtools.build.lib.collect.compacthashset.CompactHashSet;
import com.google.devtools.build.lib.concurrent.BlockingStack;
import com.google.devtools.build.lib.concurrent.MultisetSemaphore;
import com.google.devtools.build.lib.concurrent.ThreadSafety.ThreadSafe;
import com.google.devtools.build.lib.events.DelegatingEventHandler;
import com.google.devtools.build.lib.events.Event;
import com.google.devtools.build.lib.events.EventKind;
import com.google.devtools.build.lib.events.ExtendedEventHandler;
import com.google.devtools.build.lib.packages.BuildFileContainsErrorsException;
import com.google.devtools.build.lib.packages.DependencyFilter;
import com.google.devtools.build.lib.packages.NoSuchPackageException;
import com.google.devtools.build.lib.packages.NoSuchTargetException;
import com.google.devtools.build.lib.packages.NoSuchThingException;
import com.google.devtools.build.lib.packages.Package;
import com.google.devtools.build.lib.packages.Rule;
import com.google.devtools.build.lib.packages.Target;
import com.google.devtools.build.lib.pkgcache.FilteringPolicy;
import com.google.devtools.build.lib.pkgcache.PathPackageLocator;
import com.google.devtools.build.lib.pkgcache.RecursivePackageProvider;
import com.google.devtools.build.lib.pkgcache.TargetPatternEvaluator;
import com.google.devtools.build.lib.profiler.AutoProfiler;
import com.google.devtools.build.lib.query2.engine.AllRdepsFunction;
import com.google.devtools.build.lib.query2.engine.Callback;
import com.google.devtools.build.lib.query2.engine.FunctionExpression;
import com.google.devtools.build.lib.query2.engine.KeyExtractor;
import com.google.devtools.build.lib.query2.engine.MinDepthUniquifier;
import com.google.devtools.build.lib.query2.engine.OutputFormatterCallback;
import com.google.devtools.build.lib.query2.engine.QueryEvalResult;
import com.google.devtools.build.lib.query2.engine.QueryException;
import com.google.devtools.build.lib.query2.engine.QueryExpression;
import com.google.devtools.build.lib.query2.engine.QueryExpressionMapper;
import com.google.devtools.build.lib.query2.engine.QueryUtil.MinDepthUniquifierImpl;
import com.google.devtools.build.lib.query2.engine.QueryUtil.MutableKeyExtractorBackedMapImpl;
import com.google.devtools.build.lib.query2.engine.QueryUtil.ThreadSafeMutableKeyExtractorBackedSetImpl;
import com.google.devtools.build.lib.query2.engine.QueryUtil.UniquifierImpl;
import com.google.devtools.build.lib.query2.engine.RdepsFunction;
import com.google.devtools.build.lib.query2.engine.StreamableQueryEnvironment;
import com.google.devtools.build.lib.query2.engine.TargetLiteral;
import com.google.devtools.build.lib.query2.engine.ThreadSafeOutputFormatterCallback;
import com.google.devtools.build.lib.query2.engine.Uniquifier;
import com.google.devtools.build.lib.query2.engine.VariableContext;
import com.google.devtools.build.lib.skyframe.BlacklistedPackagePrefixesValue;
import com.google.devtools.build.lib.skyframe.ContainingPackageLookupFunction;
import com.google.devtools.build.lib.skyframe.FileStateValue;
import com.google.devtools.build.lib.skyframe.GraphBackedRecursivePackageProvider;
import com.google.devtools.build.lib.skyframe.PackageLookupValue;
import com.google.devtools.build.lib.skyframe.PackageValue;
import com.google.devtools.build.lib.skyframe.PrepareDepsOfPatternsFunction;
import com.google.devtools.build.lib.skyframe.RecursivePackageProviderBackedTargetPatternResolver;
import com.google.devtools.build.lib.skyframe.TargetPatternValue;
import com.google.devtools.build.lib.skyframe.TargetPatternValue.TargetPatternKey;
import com.google.devtools.build.lib.skyframe.TransitiveTraversalValue;
import com.google.devtools.build.lib.vfs.Path;
import com.google.devtools.build.lib.vfs.PathFragment;
import com.google.devtools.build.lib.vfs.RootedPath;
import com.google.devtools.build.skyframe.EvaluationResult;
import com.google.devtools.build.skyframe.InterruptibleSupplier;
import com.google.devtools.build.skyframe.SkyFunctionName;
import com.google.devtools.build.skyframe.SkyKey;
import com.google.devtools.build.skyframe.SkyValue;
import com.google.devtools.build.skyframe.WalkableGraph;
import com.google.devtools.build.skyframe.WalkableGraph.WalkableGraphFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * {@link AbstractBlazeQueryEnvironment} that introspects the Skyframe graph to find forward and
 * reverse edges. Results obtained by calling {@link #evaluateQuery} are not guaranteed to be in any
 * particular order. As well, this class eagerly loads the full transitive closure of targets, even
 * if the full closure isn't needed.
 *
 * <p>This class has concurrent implementations of the
 * {@link QueryTaskFuture}/{@link QueryTaskCallable} helper methods. The combination of this and the
 * asynchronous evaluation model yields parallel query evaluation.
 */
public class SkyQueryEnvironment extends AbstractBlazeQueryEnvironment<Target>
    implements StreamableQueryEnvironment<Target> {
  // 10k is likely a good balance between using batch efficiently and not blowing up memory.
  // TODO(janakr): Unify with RecursivePackageProviderBackedTargetPatternResolver's constant.
  protected static final int BATCH_CALLBACK_SIZE = 10000;
  protected static final int DEFAULT_THREAD_COUNT = Runtime.getRuntime().availableProcessors();
  private static final int MAX_QUERY_EXPRESSION_LOG_CHARS = 1000;
  private static final Logger logger = Logger.getLogger(SkyQueryEnvironment.class.getName());

  private final BlazeTargetAccessor accessor = new BlazeTargetAccessor(this);
  protected final int loadingPhaseThreads;
  protected final WalkableGraphFactory graphFactory;
  protected final ImmutableList<String> universeScope;
  protected boolean blockUniverseEvaluationErrors;
  protected ExtendedEventHandler universeEvalEventHandler;

  protected final String parserPrefix;
  protected final PathPackageLocator pkgPath;
  private final int queryEvaluationParallelismLevel;

  // The following fields are set in the #beforeEvaluateQuery method.
  private MultisetSemaphore<PackageIdentifier> packageSemaphore;
  protected WalkableGraph graph;
  private InterruptibleSupplier<ImmutableSet<PathFragment>> blacklistPatternsSupplier;
  private GraphBackedRecursivePackageProvider graphBackedRecursivePackageProvider;
  private ListeningExecutorService executor;
  private RecursivePackageProviderBackedTargetPatternResolver resolver;
  protected final SkyKey universeKey;
  private final ImmutableList<TargetPatternKey> universeTargetPatternKeys;

  public SkyQueryEnvironment(
      boolean keepGoing,
      int loadingPhaseThreads,
      ExtendedEventHandler eventHandler,
      Set<Setting> settings,
      Iterable<QueryFunction> extraFunctions,
      String parserPrefix,
      WalkableGraphFactory graphFactory,
      List<String> universeScope,
      PathPackageLocator pkgPath,
      boolean blockUniverseEvaluationErrors) {
    this(
        keepGoing,
        loadingPhaseThreads,
        // SkyQueryEnvironment operates on a prepopulated Skyframe graph. Therefore, query
        // evaluation is completely CPU-bound.
        /*queryEvaluationParallelismLevel=*/ DEFAULT_THREAD_COUNT,
        eventHandler,
        settings,
        extraFunctions,
        parserPrefix,
        graphFactory,
        universeScope,
        pkgPath,
        blockUniverseEvaluationErrors);
  }

  protected SkyQueryEnvironment(
      boolean keepGoing,
      int loadingPhaseThreads,
      int queryEvaluationParallelismLevel,
      ExtendedEventHandler eventHandler,
      Set<Setting> settings,
      Iterable<QueryFunction> extraFunctions,
      String parserPrefix,
      WalkableGraphFactory graphFactory,
      List<String> universeScope,
      PathPackageLocator pkgPath,
      boolean blockUniverseEvaluationErrors) {
    super(
        keepGoing,
        /*strictScope=*/ true,
        /*labelFilter=*/ Rule.ALL_LABELS,
        eventHandler,
        settings,
        extraFunctions);
    this.loadingPhaseThreads = loadingPhaseThreads;
    this.graphFactory = graphFactory;
    this.pkgPath = pkgPath;
    this.universeScope = ImmutableList.copyOf(Preconditions.checkNotNull(universeScope));
    this.parserPrefix = parserPrefix;
    Preconditions.checkState(
        !universeScope.isEmpty(), "No queries can be performed with an empty universe");
    this.queryEvaluationParallelismLevel = queryEvaluationParallelismLevel;
    this.universeKey = graphFactory.getUniverseKey(universeScope, parserPrefix);
    this.blockUniverseEvaluationErrors = blockUniverseEvaluationErrors;
    this.universeEvalEventHandler =
        this.blockUniverseEvaluationErrors
            ? new ErrorBlockingForwardingEventHandler(this.eventHandler)
            : this.eventHandler;
    this.universeTargetPatternKeys =
        PrepareDepsOfPatternsFunction.getTargetPatternKeys(
            PrepareDepsOfPatternsFunction.getSkyKeys(universeKey, eventHandler));
  }

  @Override
  public void close() {
    if (executor != null) {
      executor.shutdownNow();
      executor = null;
    }
  }

  /** Gets roots of graph which contains all nodes needed to evaluate {@code expr}. */
  protected Set<SkyKey> getGraphRootsFromExpression(QueryExpression expr)
      throws QueryException, InterruptedException {
    return ImmutableSet.of(universeKey);
  }

  private void beforeEvaluateQuery(QueryExpression expr)
      throws QueryException, InterruptedException {
    Set<SkyKey> roots = getGraphRootsFromExpression(expr);

    EvaluationResult<SkyValue> result;
    try (AutoProfiler p = AutoProfiler.logged("evaluation and walkable graph", logger)) {
      result = graphFactory.prepareAndGet(roots, loadingPhaseThreads, universeEvalEventHandler);
    }

    if (graph == null || graph != result.getWalkableGraph()) {
      checkEvaluationResult(roots, result);
      packageSemaphore = makeFreshPackageMultisetSemaphore();
      graph = result.getWalkableGraph();
      blacklistPatternsSupplier = InterruptibleSupplier.Memoize.of(new BlacklistSupplier(graph));
      graphBackedRecursivePackageProvider =
          new GraphBackedRecursivePackageProvider(graph, universeTargetPatternKeys, pkgPath);
    }

    if (executor == null) {
      executor = MoreExecutors.listeningDecorator(
          new ThreadPoolExecutor(
            /*corePoolSize=*/ queryEvaluationParallelismLevel,
            /*maximumPoolSize=*/ queryEvaluationParallelismLevel,
            /*keepAliveTime=*/ 1,
            /*units=*/ TimeUnit.SECONDS,
            /*workQueue=*/ new BlockingStack<Runnable>(),
            new ThreadFactoryBuilder().setNameFormat("QueryEnvironment %d").build()));
    }
    resolver =
        createTargetPatternResolver(
            graphBackedRecursivePackageProvider,
            eventHandler,
            TargetPatternEvaluator.DEFAULT_FILTERING_POLICY,
            packageSemaphore);
  }

  protected RecursivePackageProviderBackedTargetPatternResolver createTargetPatternResolver(
      RecursivePackageProvider graphBackedRecursivePackageProvider,
      ExtendedEventHandler eventHandler,
      FilteringPolicy policy,
      MultisetSemaphore<PackageIdentifier> packageSemaphore) {
    return new RecursivePackageProviderBackedTargetPatternResolver(
        graphBackedRecursivePackageProvider, eventHandler, policy, packageSemaphore);
  }

  protected MultisetSemaphore<PackageIdentifier> makeFreshPackageMultisetSemaphore() {
    return MultisetSemaphore.unbounded();
  }

  @ThreadSafe
  public MultisetSemaphore<PackageIdentifier> getPackageMultisetSemaphore() {
    return packageSemaphore;
  }

  protected void checkEvaluationResult(Set<SkyKey> roots, EvaluationResult<SkyValue> result)
      throws QueryException {
    // If the only root is the universe key, we expect to see either a single successfully evaluated
    // value or a cycle in the result.
    if (roots.size() == 1 && Iterables.getOnlyElement(roots).equals(universeKey)) {
      Collection<SkyValue> values = result.values();
      if (!values.isEmpty()) {
        Preconditions.checkState(
            values.size() == 1,
            "Universe query \"%s\" returned multiple values unexpectedly (%s values in result)",
            universeScope,
            values.size());
        Preconditions.checkNotNull(result.get(universeKey), result);
      } else {
        // No values in the result, so there must be an error. We expect the error to be a cycle.
        boolean foundCycle = !Iterables.isEmpty(result.getError().getCycleInfo());
        Preconditions.checkState(
            foundCycle,
            "Universe query \"%s\" failed with non-cycle error: %s",
            universeScope,
            result.getError());
      }
    }
  }

  /**
   * A {@link QueryExpressionMapper} that transforms each occurrence of an expression of the form
   * {@literal 'rdeps(<universeScope>, <T>)'} to {@literal 'allrdeps(<T>)'}. The latter is more
   * efficient.
   */
  protected static class RdepsToAllRdepsQueryExpressionMapper extends QueryExpressionMapper<Void> {
    protected final TargetPattern.Parser targetPatternParser;
    private final String absoluteUniverseScopePattern;

    protected RdepsToAllRdepsQueryExpressionMapper(
        TargetPattern.Parser targetPatternParser,
        String universeScopePattern) {
      this.targetPatternParser = targetPatternParser;
      this.absoluteUniverseScopePattern = targetPatternParser.absolutize(universeScopePattern);
    }

    @Override
    public QueryExpression visit(FunctionExpression functionExpression, Void context) {
      if (functionExpression.getFunction().getName().equals(new RdepsFunction().getName())) {
        List<Argument> args = functionExpression.getArgs();
        QueryExpression universeExpression = args.get(0).getExpression();
        if (universeExpression instanceof TargetLiteral) {
          TargetLiteral literalUniverseExpression = (TargetLiteral) universeExpression;
          String absolutizedUniverseExpression =
              targetPatternParser.absolutize(literalUniverseExpression.getPattern());
          if (absolutizedUniverseExpression.equals(absoluteUniverseScopePattern)) {
            List<Argument> argsTail = args.subList(1, functionExpression.getArgs().size());
            return new FunctionExpression(new AllRdepsFunction(), argsTail);
          }
        }
      }
      return super.visit(functionExpression, context);
    }
  }

  @Override
  public final QueryExpression transformParsedQuery(QueryExpression queryExpression) {
    QueryExpressionMapper<Void> mapper = getQueryExpressionMapper();
    QueryExpression transformedQueryExpression = queryExpression.accept(mapper);
    logger.info(
        String.format(
            "transformed query [%s] to [%s]",
            Ascii.truncate(
                queryExpression.toString(), MAX_QUERY_EXPRESSION_LOG_CHARS, "[truncated]"),
            Ascii.truncate(
                transformedQueryExpression.toString(),
                MAX_QUERY_EXPRESSION_LOG_CHARS,
                "[truncated]")));
    return transformedQueryExpression;
  }

  protected QueryExpressionMapper<Void> getQueryExpressionMapper() {
    if (universeScope.size() != 1) {
      return QueryExpressionMapper.identity();
    }
    TargetPattern.Parser targetPatternParser = new TargetPattern.Parser(parserPrefix);
    String universeScopePattern = Iterables.getOnlyElement(universeScope);
    return new RdepsToAllRdepsQueryExpressionMapper(targetPatternParser, universeScopePattern);
  }

  @Override
  protected void evalTopLevelInternal(
      QueryExpression expr, OutputFormatterCallback<Target> callback)
          throws QueryException, InterruptedException {
    Throwable throwableToThrow = null;
    try {
      super.evalTopLevelInternal(expr, callback);
    } catch (Throwable throwable) {
      throwableToThrow = throwable;
    } finally {
      if (throwableToThrow != null) {
        logger.log(
            Level.INFO,
            "About to shutdown query threadpool because of throwable",
            throwableToThrow);
        // Force termination of remaining tasks if evaluation failed abruptly (e.g. was
        // interrupted). We don't want to leave any dangling threads running tasks.
        executor.shutdownNow();
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.MILLISECONDS);
        // Signal that executor must be recreated on the next invocation.
        executor = null;
        Throwables.propagateIfPossible(
            throwableToThrow, QueryException.class, InterruptedException.class);
      }
    }
  }

  @Override
  public QueryEvalResult evaluateQuery(
      QueryExpression expr, ThreadSafeOutputFormatterCallback<Target> callback)
          throws QueryException, InterruptedException, IOException {
    beforeEvaluateQuery(expr);

    // SkyQueryEnvironment batches callback invocations using a BatchStreamedCallback, created here
    // so that there's one per top-level evaluateQuery call. The batch size is large enough that
    // per-call costs of calling the original callback are amortized over a good number of targets,
    // and small enough that holding a batch of targets in memory doesn't risk an OOM error.
    //
    // This flushes the batched callback prior to constructing the QueryEvalResult in the unlikely
    // case of a race between the original callback and the eventHandler.
    BatchStreamedCallback batchCallback = new BatchStreamedCallback(callback, BATCH_CALLBACK_SIZE);
    return super.evaluateQuery(expr, batchCallback);
  }

  private Map<SkyKey, Collection<Target>> targetifyValues(
      Map<SkyKey, ? extends Iterable<SkyKey>> input) throws InterruptedException {
    return targetifyValues(
        input,
        makePackageKeyToTargetKeyMap(ImmutableSet.copyOf(Iterables.concat(input.values()))));
  }

  private Map<SkyKey, Collection<Target>> targetifyValues(
      Map<SkyKey, ? extends Iterable<SkyKey>> input,
      Multimap<SkyKey, SkyKey> packageKeyToTargetKeyMap) throws InterruptedException {
    ImmutableMap.Builder<SkyKey, Collection<Target>> result = ImmutableMap.builder();

    Map<SkyKey, Target> allTargets =
        makeTargetsFromPackageKeyToTargetKeyMap(packageKeyToTargetKeyMap);

    for (Map.Entry<SkyKey, ? extends Iterable<SkyKey>> entry : input.entrySet()) {
      Iterable<SkyKey> skyKeys = entry.getValue();
      Set<Target> targets = CompactHashSet.createWithExpectedSize(Iterables.size(skyKeys));
      for (SkyKey key : skyKeys) {
        Target target = allTargets.get(key);
        if (target != null) {
          targets.add(target);
        }
      }
      result.put(entry.getKey(), targets);
    }
    return result.build();
  }

  private Map<SkyKey, Collection<Target>> getRawReverseDeps(
      Iterable<SkyKey> transitiveTraversalKeys) throws InterruptedException {
    return targetifyValues(graph.getReverseDeps(transitiveTraversalKeys));
  }

  private Set<Label> getAllowedDeps(Rule rule) throws InterruptedException {
    Set<Label> allowedLabels = new HashSet<>(rule.getTransitions(dependencyFilter).values());
    allowedLabels.addAll(rule.getVisibility().getDependencyLabels());
    // We should add deps from aspects, otherwise they are going to be filtered out.
    allowedLabels.addAll(rule.getAspectLabelsSuperset(dependencyFilter));
    return allowedLabels;
  }

  private Collection<Target> filterFwdDeps(Target target, Collection<Target> rawFwdDeps)
      throws InterruptedException {
    if (!(target instanceof Rule)) {
      return rawFwdDeps;
    }
    final Set<Label> allowedLabels = getAllowedDeps((Rule) target);
    return Collections2.filter(rawFwdDeps,
        new Predicate<Target>() {
          @Override
          public boolean apply(Target target) {
            return allowedLabels.contains(target.getLabel());
          }
        });
  }

  @Override
  public ThreadSafeMutableSet<Target> getFwdDeps(Iterable<Target> targets)
      throws InterruptedException {
    Map<SkyKey, Target> targetsByKey = Maps.newHashMapWithExpectedSize(Iterables.size(targets));
    for (Target target : targets) {
      targetsByKey.put(TARGET_TO_SKY_KEY.apply(target), target);
    }
    Map<SkyKey, Collection<Target>> directDeps = targetifyValues(
        graph.getDirectDeps(targetsByKey.keySet()));
    if (targetsByKey.keySet().size() != directDeps.keySet().size()) {
      Iterable<Label> missingTargets = Iterables.transform(
          Sets.difference(targetsByKey.keySet(), directDeps.keySet()),
          SKYKEY_TO_LABEL);
      eventHandler.handle(Event.warn("Targets were missing from graph: " + missingTargets));
    }
    ThreadSafeMutableSet<Target> result = createThreadSafeMutableSet();
    for (Map.Entry<SkyKey, Collection<Target>> entry : directDeps.entrySet()) {
      result.addAll(filterFwdDeps(targetsByKey.get(entry.getKey()), entry.getValue()));
    }
    return result;
  }

  /**
   * Returns deps in the form of {@link SkyKey}s.
   *
   * <p>The implementation of this method does not filter deps, therefore it is expected to be used
   * only when {@link SkyQueryEnvironment#dependencyFilter} is set to {@link
   * DependencyFilter#ALL_DEPS}.
   */
  Multimap<SkyKey, SkyKey> getDirectDepsOfSkyKeys(Iterable<SkyKey> keys)
      throws InterruptedException {
    Preconditions.checkState(dependencyFilter == DependencyFilter.ALL_DEPS, dependencyFilter);
    ImmutableMultimap.Builder<SkyKey, SkyKey> builder = ImmutableMultimap.builder();
    graph.getDirectDeps(keys).forEach(builder::putAll);
    return builder.build();
  }

  @Override
  public Collection<Target> getReverseDeps(Iterable<Target> targets) throws InterruptedException {
    return getReverseDepsOfTransitiveTraversalKeys(Iterables.transform(targets, TARGET_TO_SKY_KEY));
  }

  private Collection<Target> getReverseDepsOfTransitiveTraversalKeys(
      Iterable<SkyKey> transitiveTraversalKeys) throws InterruptedException {
    Map<SkyKey, Collection<Target>> rawReverseDeps = getRawReverseDeps(transitiveTraversalKeys);
    return processRawReverseDeps(rawReverseDeps);
  }

  /** Targetify SkyKeys of reverse deps and filter out targets whose deps are not allowed. */
  Collection<Target> filterRawReverseDepsOfTransitiveTraversalKeys(
      Map<SkyKey, ? extends Iterable<SkyKey>> rawReverseDeps,
      Multimap<SkyKey, SkyKey> packageKeyToTargetKeyMap) throws InterruptedException {
    return processRawReverseDeps(targetifyValues(rawReverseDeps, packageKeyToTargetKeyMap));
  }

  private Collection<Target> processRawReverseDeps(Map<SkyKey, Collection<Target>> rawReverseDeps)
      throws InterruptedException {
    Set<Target> result = CompactHashSet.create();
    CompactHashSet<Target> visited =
        CompactHashSet.createWithExpectedSize(totalSizeOfCollections(rawReverseDeps.values()));

    Set<Label> keys = CompactHashSet.create(Collections2.transform(rawReverseDeps.keySet(),
        SKYKEY_TO_LABEL));
    for (Collection<Target> parentCollection : rawReverseDeps.values()) {
      for (Target parent : parentCollection) {
        if (visited.add(parent)) {
          if (parent instanceof Rule && dependencyFilter != DependencyFilter.ALL_DEPS) {
            for (Label label : getAllowedDeps((Rule) parent)) {
              if (keys.contains(label)) {
                result.add(parent);
              }
            }
          } else {
            result.add(parent);
          }
        }
      }
    }
    return result;
  }

  private static <T> int totalSizeOfCollections(Iterable<Collection<T>> nestedCollections) {
    int totalSize = 0;
    for (Collection<T> collection : nestedCollections) {
      totalSize += collection.size();
    }
    return totalSize;
  }

  @Override
  public ThreadSafeMutableSet<Target> getTransitiveClosure(ThreadSafeMutableSet<Target> targets)
      throws InterruptedException {
    return SkyQueryUtils.getTransitiveClosure(
        targets, this::getFwdDeps, createThreadSafeMutableSet());
  }

  @Override
  public ImmutableList<Target> getNodesOnPath(Target from, Target to)
      throws InterruptedException {
    return SkyQueryUtils.getNodesOnPath(from, to, this::getFwdDeps, Target::getLabel);
  }

  private <R> ListenableFuture<R> safeSubmit(Callable<R> callable) {
    try {
      return executor.submit(callable);
    } catch (RejectedExecutionException e) {
      return Futures.immediateCancelledFuture();
    }
  }

  private <R> ListenableFuture<R> safeSubmitAsync(AsyncCallable<R> callable) {
    try {
      return Futures.submitAsync(callable, executor);
    } catch (RejectedExecutionException e) {
      return Futures.immediateCancelledFuture();
    }
  }

  @ThreadSafe
  @Override
  public QueryTaskFuture<Void> eval(
      final QueryExpression expr,
      final VariableContext<Target> context,
      final Callback<Target> callback) {
    // TODO(bazel-team): As in here, use concurrency for the async #eval of other QueryEnvironment
    // implementations.
    AsyncCallable<Void> task =
        () -> (QueryTaskFutureImpl<Void>) expr.eval(SkyQueryEnvironment.this, context, callback);
    return QueryTaskFutureImpl.ofDelegate(safeSubmitAsync(task));
  }

  @Override
  public <R> QueryTaskFuture<R> executeAsync(QueryTaskCallable<R> callable) {
    return QueryTaskFutureImpl.ofDelegate(safeSubmit(callable));
  }

  @Override
  public <T1, T2> QueryTaskFuture<T2> transformAsync(
      QueryTaskFuture<T1> future,
      final Function<T1, QueryTaskFuture<T2>> function) {
    return QueryTaskFutureImpl.ofDelegate(
        Futures.transformAsync(
            (QueryTaskFutureImpl<T1>) future,
            input -> (QueryTaskFutureImpl<T2>) function.apply(input),
            executor));
  }

  @Override
  public <R> QueryTaskFuture<R> whenAllSucceedCall(
      Iterable<? extends QueryTaskFuture<?>> futures, QueryTaskCallable<R> callable) {
    return QueryTaskFutureImpl.ofDelegate(
        Futures.whenAllSucceed(cast(futures)).call(callable, executor));
  }

  @ThreadSafe
  @Override
  public ThreadSafeMutableSet<Target> createThreadSafeMutableSet() {
    return new ThreadSafeMutableKeyExtractorBackedSetImpl<>(
        TargetKeyExtractor.INSTANCE, Target.class, DEFAULT_THREAD_COUNT);
  }

  @Override
  public <V> MutableMap<Target, V> createMutableMap() {
    return new MutableKeyExtractorBackedMapImpl<Target, Label, V>(TargetKeyExtractor.INSTANCE);
  }

  @ThreadSafe
  @Override
  public Uniquifier<Target> createUniquifier() {
    return createTargetUniquifier();
  }

  @ThreadSafe
  @Override
  public MinDepthUniquifier<Target> createMinDepthUniquifier() {
    return new MinDepthUniquifierImpl<>(TargetKeyExtractor.INSTANCE, DEFAULT_THREAD_COUNT);
  }

  @ThreadSafe
  protected MinDepthUniquifier<SkyKey> createMinDepthSkyKeyUniquifier() {
    return new MinDepthUniquifierImpl<>(SkyKeyKeyExtractor.INSTANCE, DEFAULT_THREAD_COUNT);
  }

  @ThreadSafe
  Uniquifier<Target> createTargetUniquifier() {
    return new UniquifierImpl<>(TargetKeyExtractor.INSTANCE, DEFAULT_THREAD_COUNT);
  }

  @ThreadSafe
  protected Uniquifier<SkyKey> createSkyKeyUniquifier() {
    return new UniquifierImpl<>(SkyKeyKeyExtractor.INSTANCE, DEFAULT_THREAD_COUNT);
  }

  private ImmutableSet<PathFragment> getBlacklistedExcludes(TargetPatternKey targetPatternKey)
  throws InterruptedException {
    return targetPatternKey.getAllBlacklistedSubdirectoriesToExclude(blacklistPatternsSupplier);
  }

  @ThreadSafe
  @Override
  public Collection<Target> getSiblingTargetsInPackage(Target target) {
    return target.getPackage().getTargets().values();
  }

  @ThreadSafe
  @Override
  public QueryTaskFuture<Void> getTargetsMatchingPattern(
      QueryExpression owner, String pattern, Callback<Target> callback) {
    TargetPatternKey targetPatternKey;
    try {
      targetPatternKey = TargetPatternValue.key(
          pattern, TargetPatternEvaluator.DEFAULT_FILTERING_POLICY, parserPrefix);
    } catch (TargetParsingException tpe) {
      try {
        reportBuildFileError(owner, tpe.getMessage());
      } catch (QueryException qe) {
        return immediateFailedFuture(qe);
      }
      return immediateSuccessfulFuture(null);
    }
    return evalTargetPatternKey(owner, targetPatternKey, callback);
  }

  @ThreadSafe
  public QueryTaskFuture<Void> evalTargetPatternKey(
      QueryExpression owner, TargetPatternKey targetPatternKey, Callback<Target> callback) {
    ImmutableSet<PathFragment> blacklistedSubdirectoriesToExclude;
    try {
      blacklistedSubdirectoriesToExclude = getBlacklistedExcludes(targetPatternKey);
    } catch (InterruptedException ie) {
      return immediateCancelledFuture();
    }
    TargetPattern patternToEval = targetPatternKey.getParsedPattern();
    ImmutableSet<PathFragment> additionalSubdirectoriesToExclude =
        targetPatternKey.getExcludedSubdirectories();
    AsyncFunction<TargetParsingException, Void> reportBuildFileErrorAsyncFunction =
        exn -> {
          reportBuildFileError(owner, exn.getMessage());
          return Futures.immediateFuture(null);
        };
    ListenableFuture<Void> evalFuture = patternToEval.evalAsync(
        resolver,
        blacklistedSubdirectoriesToExclude,
        additionalSubdirectoriesToExclude,
        callback,
        QueryException.class,
        executor);
    return QueryTaskFutureImpl.ofDelegate(
        Futures.catchingAsync(
            evalFuture,
            TargetParsingException.class,
            reportBuildFileErrorAsyncFunction,
            directExecutor()));
  }

  @ThreadSafe
  @Override
  public ThreadSafeMutableSet<Target> getBuildFiles(
      QueryExpression caller, ThreadSafeMutableSet<Target> nodes, boolean buildFiles, boolean loads)
      throws QueryException, InterruptedException {
    ThreadSafeMutableSet<Target> dependentFiles = createThreadSafeMutableSet();
    Set<PackageIdentifier> seenPackages = new HashSet<>();
    // Keep track of seen labels, to avoid adding a fake subinclude label that also exists as a
    // real target.
    Set<Label> seenLabels = new HashSet<>();

    // Adds all the package definition files (BUILD files and build
    // extensions) for package "pkg", to "buildfiles".
    for (Target x : nodes) {
      Package pkg = x.getPackage();
      if (seenPackages.add(pkg.getPackageIdentifier())) {
        if (buildFiles) {
          addIfUniqueLabel(pkg.getBuildFile(), seenLabels, dependentFiles);
        }

        List<Label> extensions = new ArrayList<>();
        if (loads) {
          extensions.addAll(pkg.getSkylarkFileDependencies());
        }

        for (Label extension : extensions) {

          Target loadTarget = getLoadTarget(extension, pkg);
          addIfUniqueLabel(loadTarget, seenLabels, dependentFiles);

          // Also add the BUILD file of the extension.
          if (buildFiles) {
            Label buildFileLabel = getBuildFileLabel(loadTarget.getLabel().getPackageIdentifier());
            addIfUniqueLabel(new FakeLoadTarget(buildFileLabel, pkg), seenLabels, dependentFiles);
          }
        }
      }
    }
    return dependentFiles;
  }

  protected Label getBuildFileLabel(PackageIdentifier packageIdentifier) throws QueryException {
    // TODO(bazel-team): Try avoid filesystem access here.
    Path buildFileForLoad = null;
    try {
      buildFileForLoad = pkgPath.getPackageBuildFile(packageIdentifier);
    } catch (NoSuchPackageException e) {
      throw new QueryException(packageIdentifier + " does not exist in graph");
    }
    return Label.createUnvalidated(packageIdentifier, buildFileForLoad.getBaseName());
  }

  private static void addIfUniqueLabel(Target node, Set<Label> labels, Set<Target> nodes) {
    if (labels.add(node.getLabel())) {
      nodes.add(node);
    }
  }

  private Target getLoadTarget(Label label, Package pkg) {
    return new FakeLoadTarget(label, pkg);
  }

  @ThreadSafe
  @Override
  public TargetAccessor<Target> getAccessor() {
    return accessor;
  }

  @ThreadSafe
  private Package getPackage(PackageIdentifier packageIdentifier)
      throws InterruptedException, QueryException, NoSuchPackageException {
    SkyKey packageKey = PackageValue.key(packageIdentifier);
      PackageValue packageValue = (PackageValue) graph.getValue(packageKey);
      if (packageValue != null) {
        Package pkg = packageValue.getPackage();
        if (pkg.containsErrors()) {
        throw new BuildFileContainsErrorsException(packageIdentifier);
        }
      return pkg;
      } else {
      NoSuchPackageException exception = (NoSuchPackageException) graph.getException(packageKey);
        if (exception != null) {
          throw exception;
        }
        if (graph.isCycle(packageKey)) {
        throw new NoSuchPackageException(packageIdentifier, "Package depends on a cycle");
        } else {
          throw new QueryException(packageKey + " does not exist in graph");
        }
      }
  }

  @ThreadSafe
  @Override
  public Target getTarget(Label label)
      throws TargetNotFoundException, QueryException, InterruptedException {
    try {
      Package pkg = getPackage(label.getPackageIdentifier());
      return pkg.getTarget(label.getName());
    } catch (NoSuchThingException e) {
      throw new TargetNotFoundException(e);
    }
  }

  @ThreadSafe
  public Map<PackageIdentifier, Package> bulkGetPackages(Iterable<PackageIdentifier> pkgIds)
      throws InterruptedException {
    Set<SkyKey> pkgKeys = ImmutableSet.copyOf(PackageValue.keys(pkgIds));
    ImmutableMap.Builder<PackageIdentifier, Package> pkgResults = ImmutableMap.builder();
    Map<SkyKey, SkyValue> packages = graph.getSuccessfulValues(pkgKeys);
    for (Map.Entry<SkyKey, SkyValue> pkgEntry : packages.entrySet()) {
      PackageIdentifier pkgId = (PackageIdentifier) pkgEntry.getKey().argument();
      PackageValue pkgValue = (PackageValue) pkgEntry.getValue();
      pkgResults.put(pkgId, Preconditions.checkNotNull(pkgValue.getPackage(), pkgId));
    }
    return pkgResults.build();
  }

  @Override
  public void buildTransitiveClosure(
      QueryExpression caller,
      ThreadSafeMutableSet<Target> targets,
      int maxDepth) throws QueryException, InterruptedException {
    // Everything has already been loaded, so here we just check for errors so that we can
    // pre-emptively throw/report if needed.
    Iterable<SkyKey> transitiveTraversalKeys = makeTransitiveTraversalKeys(targets);
    ImmutableList.Builder<String> errorMessagesBuilder = ImmutableList.builder();

    // First, look for errors in the successfully evaluated TransitiveTraversalValues. They may
    // have encountered errors that they were able to recover from.
    Set<Map.Entry<SkyKey, SkyValue>> successfulEntries =
        graph.getSuccessfulValues(transitiveTraversalKeys).entrySet();
    ImmutableSet.Builder<SkyKey> successfulKeysBuilder = ImmutableSet.builder();
    for (Map.Entry<SkyKey, SkyValue> successfulEntry : successfulEntries) {
      successfulKeysBuilder.add(successfulEntry.getKey());
      TransitiveTraversalValue value = (TransitiveTraversalValue) successfulEntry.getValue();
      String firstErrorMessage = value.getFirstErrorMessage();
      if (firstErrorMessage != null) {
        errorMessagesBuilder.add(firstErrorMessage);
      }
    }
    ImmutableSet<SkyKey> successfulKeys = successfulKeysBuilder.build();

    // Next, look for errors from the unsuccessfully evaluated TransitiveTraversal skyfunctions.
    Iterable<SkyKey> unsuccessfulKeys =
        Iterables.filter(transitiveTraversalKeys, Predicates.not(Predicates.in(successfulKeys)));
    Set<Map.Entry<SkyKey, Exception>> errorEntries =
        graph.getMissingAndExceptions(unsuccessfulKeys).entrySet();
    for (Map.Entry<SkyKey, Exception> entry : errorEntries) {
      if (entry.getValue() == null) {
        // Targets may be in the graph because they are not in the universe or depend on cycles.
        eventHandler.handle(Event.warn(entry.getKey().argument() + " does not exist in graph"));
      } else {
        errorMessagesBuilder.add(entry.getValue().getMessage());
      }
    }

    // Lastly, report all found errors.
    ImmutableList<String> errorMessages = errorMessagesBuilder.build();
    for (String errorMessage : errorMessages) {
      reportBuildFileError(caller, errorMessage);
    }
  }

  @Override
  protected void preloadOrThrow(QueryExpression caller, Collection<String> patterns)
      throws QueryException, TargetParsingException {
    // SkyQueryEnvironment directly evaluates target patterns in #getTarget and similar methods
    // using its graph, which is prepopulated using the universeScope (see #beforeEvaluateQuery),
    // so no preloading of target patterns is necessary.
  }

  static final Predicate<SkyKey> IS_TTV = SkyFunctionName.functionIs(Label.TRANSITIVE_TRAVERSAL);

  static final Function<SkyKey, Label> SKYKEY_TO_LABEL =
      skyKey -> IS_TTV.apply(skyKey) ? (Label) skyKey.argument() : null;


  static final Function<SkyKey, PackageIdentifier> PACKAGE_SKYKEY_TO_PACKAGE_IDENTIFIER =
      skyKey -> (PackageIdentifier) skyKey.argument();

  @ThreadSafe
  Multimap<SkyKey, SkyKey> makePackageKeyToTargetKeyMap(Iterable<SkyKey> keys) {
    Multimap<SkyKey, SkyKey> packageKeyToTargetKeyMap = ArrayListMultimap.create();
    for (SkyKey key : keys) {
      Label label = SKYKEY_TO_LABEL.apply(key);
      if (label == null) {
        continue;
      }
      packageKeyToTargetKeyMap.put(PackageValue.key(label.getPackageIdentifier()), key);
    }
    return packageKeyToTargetKeyMap;
  }

  @ThreadSafe
  public Map<SkyKey, Target> makeTargetsFromSkyKeys(Iterable<SkyKey> keys)
      throws InterruptedException {
    return makeTargetsFromPackageKeyToTargetKeyMap(makePackageKeyToTargetKeyMap(keys));
  }

  @ThreadSafe
  public Map<SkyKey, Target> makeTargetsFromPackageKeyToTargetKeyMap(
      Multimap<SkyKey, SkyKey> packageKeyToTargetKeyMap) throws InterruptedException {
    ImmutableMap.Builder<SkyKey, Target> result = ImmutableMap.builder();
    Set<SkyKey> processedTargets = new HashSet<>();
    Map<SkyKey, SkyValue> packageMap = graph.getSuccessfulValues(packageKeyToTargetKeyMap.keySet());
    for (Map.Entry<SkyKey, SkyValue> entry : packageMap.entrySet()) {
      for (SkyKey targetKey : packageKeyToTargetKeyMap.get(entry.getKey())) {
        if (processedTargets.add(targetKey)) {
          try {
            result.put(
                targetKey,
                ((PackageValue) entry.getValue())
                    .getPackage()
                    .getTarget((SKYKEY_TO_LABEL.apply(targetKey)).getName()));
          } catch (NoSuchTargetException e) {
            // Skip missing target.
          }
        }
      }
    }
    return result.build();
  }

  static final Function<Target, SkyKey> TARGET_TO_SKY_KEY =
      target -> TransitiveTraversalValue.key(target.getLabel());

  /** A strict (i.e. non-lazy) variant of {@link #makeTransitiveTraversalKeys}. */
  public static Iterable<SkyKey> makeTransitiveTraversalKeysStrict(Iterable<Target> targets) {
    return ImmutableList.copyOf(makeTransitiveTraversalKeys(targets));
  }

  private static Iterable<SkyKey> makeTransitiveTraversalKeys(Iterable<Target> targets) {
    return Iterables.transform(targets, TARGET_TO_SKY_KEY);
  }

  @Override
  public Target getOrCreate(Target target) {
    return target;
  }

  /**
   * Returns package lookup keys for looking up the package root for which there may be a relevant
   * (from the perspective of {@link #getRBuildFiles}) {@link FileStateValue} node in the graph for
   * {@code originalFileFragment}, which is assumed to be a file path.
   *
   * <p>This is a helper function for {@link #getFileStateKeysForFileFragments}.
   */
  private static Iterable<SkyKey> getPkgLookupKeysForFile(PathFragment originalFileFragment,
      PathFragment currentPathFragment) {
    if (originalFileFragment.equals(currentPathFragment)
        && originalFileFragment.equals(Label.WORKSPACE_FILE_NAME)) {
      Preconditions.checkState(
          Label.WORKSPACE_FILE_NAME.getParentDirectory().equals(PathFragment.EMPTY_FRAGMENT),
          Label.WORKSPACE_FILE_NAME);
      return ImmutableList.of(
          PackageLookupValue.key(Label.EXTERNAL_PACKAGE_IDENTIFIER),
          PackageLookupValue.key(PackageIdentifier.createInMainRepo(PathFragment.EMPTY_FRAGMENT)));
    }
    PathFragment parentPathFragment = currentPathFragment.getParentDirectory();
    return parentPathFragment == null
        ? ImmutableList.<SkyKey>of()
        : ImmutableList.of(PackageLookupValue.key(
            PackageIdentifier.createInMainRepo(parentPathFragment)));
  }

  /**
   * Returns FileStateValue keys for which there may be relevant (from the perspective of {@link
   * #getRBuildFiles}) FileStateValues in the graph corresponding to the given
   * {@code pathFragments}, which are assumed to be file paths.
   *
   * <p>To do this, we emulate the {@link ContainingPackageLookupFunction} logic: for each given
   * file path, we look for the nearest ancestor directory (starting with its parent directory), if
   * any, that has a package. The {@link PackageLookupValue} for this package tells us the package
   * root that we should use for the {@link RootedPath} for the {@link FileStateValue} key.
   *
   * <p>Note that there may not be nodes in the graph corresponding to the returned SkyKeys.
   */
  Collection<SkyKey> getFileStateKeysForFileFragments(Iterable<PathFragment> pathFragments)
      throws InterruptedException {
    Set<SkyKey> result = new HashSet<>();
    Multimap<PathFragment, PathFragment> currentToOriginal = ArrayListMultimap.create();
    for (PathFragment pathFragment : pathFragments) {
      currentToOriginal.put(pathFragment, pathFragment);
    }
    while (!currentToOriginal.isEmpty()) {
      Multimap<SkyKey, PathFragment> packageLookupKeysToOriginal = ArrayListMultimap.create();
      Multimap<SkyKey, PathFragment> packageLookupKeysToCurrent = ArrayListMultimap.create();
      for (Map.Entry<PathFragment, PathFragment> entry : currentToOriginal.entries()) {
        PathFragment current = entry.getKey();
        PathFragment original = entry.getValue();
        for (SkyKey packageLookupKey : getPkgLookupKeysForFile(original, current)) {
          packageLookupKeysToOriginal.put(packageLookupKey, original);
          packageLookupKeysToCurrent.put(packageLookupKey, current);
        }
      }
      Map<SkyKey, SkyValue> lookupValues =
          graph.getSuccessfulValues(packageLookupKeysToOriginal.keySet());
      for (Map.Entry<SkyKey, SkyValue> entry : lookupValues.entrySet()) {
        SkyKey packageLookupKey = entry.getKey();
        PackageLookupValue packageLookupValue = (PackageLookupValue) entry.getValue();
        if (packageLookupValue.packageExists()) {
          Collection<PathFragment> originalFiles =
              packageLookupKeysToOriginal.get(packageLookupKey);
          Preconditions.checkState(!originalFiles.isEmpty(), entry);
          for (PathFragment fileName : originalFiles) {
            result.add(FileStateValue.key(
                RootedPath.toRootedPath(packageLookupValue.getRoot(), fileName)));
          }
          for (PathFragment current : packageLookupKeysToCurrent.get(packageLookupKey)) {
            currentToOriginal.removeAll(current);
          }
        }
      }
      Multimap<PathFragment, PathFragment> newCurrentToOriginal = ArrayListMultimap.create();
      for (PathFragment pathFragment : currentToOriginal.keySet()) {
        PathFragment parent = pathFragment.getParentDirectory();
        if (parent != null) {
          newCurrentToOriginal.putAll(parent, currentToOriginal.get(pathFragment));
        }
      }
      currentToOriginal = newCurrentToOriginal;
    }
    return result;
  }

  void getBuildFileTargetsForPackageKeysAndProcessViaCallback(
      Iterable<SkyKey> packageKeys, Callback<Target> callback)
      throws QueryException, InterruptedException {
    Set<PackageIdentifier> pkgIds =
          Streams.stream(packageKeys)
              .map(SkyQueryEnvironment.PACKAGE_SKYKEY_TO_PACKAGE_IDENTIFIER)
              .collect(toImmutableSet());
    packageSemaphore.acquireAll(pkgIds);
    try {
      Iterable<SkyValue> packageValues = graph.getSuccessfulValues(packageKeys).values();
      Iterable<Target> buildFileTargets = getBuildFileTargetsFromPackageValues(packageValues);
      callback.process(buildFileTargets);
    } finally {
      packageSemaphore.releaseAll(pkgIds);
    }
  }

  protected Iterable<Target> getBuildFileTargetsFromPackageValues(
      Iterable<SkyValue> packageValues) {
    // TODO(laurentlb): Use streams?
    return Iterables.transform(
        Iterables.filter(
            Iterables.transform(packageValues, skyValue -> ((PackageValue) skyValue).getPackage()),
            pkg -> !pkg.containsErrors()),
        Package::getBuildFile);
  }

  /**
   * Calculates the set of packages that transitively depend on, via load statements, the specified
   * paths. The emitted {@link Target}s are BUILD file targets.
   */
  @ThreadSafe
  QueryTaskFuture<Void> getRBuildFiles(
      Collection<PathFragment> fileIdentifiers, Callback<Target> callback) {
    return QueryTaskFutureImpl.ofDelegate(
        safeSubmit(
            () -> {
              ParallelSkyQueryUtils.getRBuildFilesParallel(
                  SkyQueryEnvironment.this, fileIdentifiers, callback);
              return null;
            }));
  }

  @Override
  public Iterable<QueryFunction> getFunctions() {
    return ImmutableList.<QueryFunction>builder()
        .addAll(super.getFunctions())
        .add(new AllRdepsFunction())
        .add(new RBuildFilesFunction())
        .build();
  }

  private static class BlacklistSupplier
      implements InterruptibleSupplier<ImmutableSet<PathFragment>> {
    private final WalkableGraph graph;

    private BlacklistSupplier(WalkableGraph graph) {
      this.graph = graph;
    }

    @Override
    public ImmutableSet<PathFragment> get() throws InterruptedException {
      return ((BlacklistedPackagePrefixesValue)
              graph.getValue(BlacklistedPackagePrefixesValue.key()))
          .getPatterns();
    }
  }

  private static class SkyKeyKeyExtractor implements KeyExtractor<SkyKey, SkyKey> {
    private static final SkyKeyKeyExtractor INSTANCE = new SkyKeyKeyExtractor();

    private SkyKeyKeyExtractor() {
    }

    @Override
    public SkyKey extractKey(SkyKey element) {
      return element;
    }
  }

  /**
   * Wraps a {@link Callback} and guarantees that all calls to the original will have at least
   * {@code batchThreshold} {@link Target}s, except for the final such call.
   *
   * <p>Retains fewer than {@code batchThreshold} {@link Target}s at a time.
   *
   * <p>After this object's {@link #process} has been called for the last time, {#link
   * #processLastPending} must be called to "flush" any remaining {@link Target}s through to the
   * original.
   *
   * <p>This callback may be called from multiple threads concurrently. At most one thread will call
   * the wrapped {@code callback} concurrently.
   */
  // TODO(nharmata): For queries with less than {@code batchThreshold} results, this batching
  // strategy probably hurts performance since we can only start formatting results once the entire
  // query is finished.
  // TODO(nharmata): This batching strategy is also potentially harmful from a memory perspective
  // since when the Targets being output are backed by Package instances, we're delaying GC of the
  // Package instances until the output batch size is met.
  private static class BatchStreamedCallback extends ThreadSafeOutputFormatterCallback<Target>
      implements Callback<Target> {

    // TODO(nharmata): Now that we know the wrapped callback is ThreadSafe, there's no correctness
    // concern that requires the prohibition of concurrent uses of the callback; the only concern is
    // memory. We should have a threshold for when to invoke the callback with a batch, and also a
    // separate, larger, bound on the number of targets being processed at the same time.
    private final ThreadSafeOutputFormatterCallback<Target> callback;
    private final Uniquifier<Target> uniquifier =
        new UniquifierImpl<>(TargetKeyExtractor.INSTANCE, DEFAULT_THREAD_COUNT);
    private final Object pendingLock = new Object();
    private List<Target> pending = new ArrayList<>();
    private int batchThreshold;

    private BatchStreamedCallback(
        ThreadSafeOutputFormatterCallback<Target> callback,
        int batchThreshold) {
      this.callback = callback;
      this.batchThreshold = batchThreshold;
    }

    @Override
    public void start() throws IOException {
      callback.start();
    }

    @Override
    public void processOutput(Iterable<Target> partialResult)
        throws IOException, InterruptedException {
      ImmutableList<Target> uniquifiedTargets = uniquifier.unique(partialResult);
      synchronized (pendingLock) {
        Preconditions.checkNotNull(pending, "Reuse of the callback is not allowed");
        pending.addAll(uniquifiedTargets);
        if (pending.size() >= batchThreshold) {
          callback.processOutput(pending);
          pending = new ArrayList<>();
        }
      }
    }

    @Override
    public void close(boolean failFast) throws IOException, InterruptedException {
      if (!failFast) {
        processLastPending();
      }
      callback.close(failFast);
    }

    private void processLastPending() throws IOException, InterruptedException {
      synchronized (pendingLock) {
        if (!pending.isEmpty()) {
          callback.processOutput(pending);
          pending = null;
        }
      }
    }
  }

  @ThreadSafe
  @Override
  public QueryTaskFuture<Void> getAllRdepsUnboundedParallel(
      QueryExpression expression,
      VariableContext<Target> context,
      Callback<Target> callback) {
    return ParallelSkyQueryUtils.getAllRdepsUnboundedParallel(
        this, expression, context, callback, packageSemaphore);
  }

  @ThreadSafe
  @Override
  public QueryTaskFuture<Void> getAllRdepsBoundedParallel(
      QueryExpression expression,
      int depth,
      VariableContext<Target> context,
      Callback<Target> callback) {
    return ParallelSkyQueryUtils.getAllRdepsBoundedParallel(
        this, expression, depth, context, callback, packageSemaphore);
  }

  protected QueryTaskFuture<Predicate<SkyKey>> getUniverseDTCSkyKeyPredicateFuture(
      QueryExpression universe,
      VariableContext<Target> context) {
    return ParallelSkyQueryUtils.getDTCSkyKeyPredicateFuture(
        this,
        universe,
        context,
        BATCH_CALLBACK_SIZE,
        DEFAULT_THREAD_COUNT);
  }

  @ThreadSafe
  @Override
  public QueryTaskFuture<Void> getRdepsUnboundedParallel(
      QueryExpression expression,
      QueryExpression universe,
      VariableContext<Target> context,
      Callback<Target> callback) {
    return transformAsync(
        getUniverseDTCSkyKeyPredicateFuture(universe, context),
        universePredicate -> ParallelSkyQueryUtils.getRdepsInUniverseUnboundedParallel(
            this, expression, universePredicate, context, callback, packageSemaphore));
  }

  @ThreadSafe
  @Override
  public QueryTaskFuture<Void> getRdepsBoundedParallel(
      QueryExpression expression,
      int depth,
      QueryExpression universe,
      VariableContext<Target> context,
      Callback<Target> callback) {
    return transformAsync(
        getUniverseDTCSkyKeyPredicateFuture(universe, context),
        universePredicate -> ParallelSkyQueryUtils.getRdepsInUniverseBoundedParallel(
            this, expression, depth, universePredicate, context, callback, packageSemaphore));
  }

  /**
   * Query evaluation behavior is specified with respect to errors it emits. (Or at least it should
   * be. Tools rely on it.) Notably, errors that occur during evaluation of a query's universe must
   * not be emitted during query command evaluation. Consider the case of a simple single target
   * query when {@code //...} is the universe: errors in far flung parts of the workspace should not
   * be emitted when that query command is evaluated.
   *
   * <p>Non-error message events are not specified. For instance, it's useful (and expected by some
   * unit tests that should know better) for query commands to emit {@link EventKind#PROGRESS}
   * events during package loading.
   *
   * <p>Therefore, this class is used to forward only non-{@link EventKind#ERROR} events during
   * universe loading to the {@link SkyQueryEnvironment}'s {@link ExtendedEventHandler}.
   */
  protected static class ErrorBlockingForwardingEventHandler extends DelegatingEventHandler {

    public ErrorBlockingForwardingEventHandler(ExtendedEventHandler delegate) {
      super(delegate);
    }

    @Override
    public void handle(Event e) {
      if (!e.getKind().equals(EventKind.ERROR)) {
        super.handle(e);
      }
    }
  }

  /** Pair of a key and a depth, useful for driving usages of {@link MinDepthUniquifier}. */
  public static class KeyAtDepth {
    public final SkyKey key;
    public final int depth;

    public KeyAtDepth(SkyKey key, int depth) {
      this.key = key;
      this.depth = depth;
    }

    @Override
    public int hashCode() {
      // N.B. - We deliberately use a garbage-free hashCode implementation (rather than e.g.
      // Objects#hash). This method is very hot during large visitations done by
      // ParallelSkyQueryUtils.
      return 31 * key.hashCode() + Integer.hashCode(depth);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof KeyAtDepth)) {
        return false;
      }
      KeyAtDepth other = (KeyAtDepth) obj;
      return key.equals(other.key) && depth == other.depth;
    }
  }
}