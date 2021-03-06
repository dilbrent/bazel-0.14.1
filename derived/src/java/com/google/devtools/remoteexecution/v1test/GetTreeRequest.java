// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/devtools/remoteexecution/v1test/remote_execution.proto

package com.google.devtools.remoteexecution.v1test;

/**
 * <pre>
 * A request message for
 * [ContentAddressableStorage.GetTree][google.devtools.remoteexecution.v1test.ContentAddressableStorage.GetTree].
 * This message is deprecated and should no longer be used.
 * </pre>
 *
 * Protobuf type {@code google.devtools.remoteexecution.v1test.GetTreeRequest}
 */
public  final class GetTreeRequest extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:google.devtools.remoteexecution.v1test.GetTreeRequest)
    GetTreeRequestOrBuilder {
private static final long serialVersionUID = 0L;
  // Use GetTreeRequest.newBuilder() to construct.
  private GetTreeRequest(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private GetTreeRequest() {
    instanceName_ = "";
    pageSize_ = 0;
    pageToken_ = "";
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return this.unknownFields;
  }
  private GetTreeRequest(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
    com.google.protobuf.UnknownFieldSet.Builder unknownFields =
        com.google.protobuf.UnknownFieldSet.newBuilder();
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!parseUnknownFieldProto3(
                input, unknownFields, extensionRegistry, tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            java.lang.String s = input.readStringRequireUtf8();

            instanceName_ = s;
            break;
          }
          case 18: {
            com.google.devtools.remoteexecution.v1test.Digest.Builder subBuilder = null;
            if (rootDigest_ != null) {
              subBuilder = rootDigest_.toBuilder();
            }
            rootDigest_ = input.readMessage(com.google.devtools.remoteexecution.v1test.Digest.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom(rootDigest_);
              rootDigest_ = subBuilder.buildPartial();
            }

            break;
          }
          case 24: {

            pageSize_ = input.readInt32();
            break;
          }
          case 34: {
            java.lang.String s = input.readStringRequireUtf8();

            pageToken_ = s;
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      this.unknownFields = unknownFields.build();
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return com.google.devtools.remoteexecution.v1test.RemoteExecutionProto.internal_static_google_devtools_remoteexecution_v1test_GetTreeRequest_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return com.google.devtools.remoteexecution.v1test.RemoteExecutionProto.internal_static_google_devtools_remoteexecution_v1test_GetTreeRequest_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            com.google.devtools.remoteexecution.v1test.GetTreeRequest.class, com.google.devtools.remoteexecution.v1test.GetTreeRequest.Builder.class);
  }

  public static final int INSTANCE_NAME_FIELD_NUMBER = 1;
  private volatile java.lang.Object instanceName_;
  /**
   * <pre>
   * The instance of the execution system to operate against. A server may
   * support multiple instances of the execution system (with their own workers,
   * storage, caches, etc.). The server MAY require use of this field to select
   * between them in an implementation-defined fashion, otherwise it can be
   * omitted.
   * </pre>
   *
   * <code>string instance_name = 1;</code>
   */
  public java.lang.String getInstanceName() {
    java.lang.Object ref = instanceName_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      instanceName_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * The instance of the execution system to operate against. A server may
   * support multiple instances of the execution system (with their own workers,
   * storage, caches, etc.). The server MAY require use of this field to select
   * between them in an implementation-defined fashion, otherwise it can be
   * omitted.
   * </pre>
   *
   * <code>string instance_name = 1;</code>
   */
  public com.google.protobuf.ByteString
      getInstanceNameBytes() {
    java.lang.Object ref = instanceName_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      instanceName_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  public static final int ROOT_DIGEST_FIELD_NUMBER = 2;
  private com.google.devtools.remoteexecution.v1test.Digest rootDigest_;
  /**
   * <pre>
   * The digest of the root, which must be an encoded
   * [Directory][google.devtools.remoteexecution.v1test.Directory] message
   * stored in the
   * [ContentAddressableStorage][google.devtools.remoteexecution.v1test.ContentAddressableStorage].
   * </pre>
   *
   * <code>.google.devtools.remoteexecution.v1test.Digest root_digest = 2;</code>
   */
  public boolean hasRootDigest() {
    return rootDigest_ != null;
  }
  /**
   * <pre>
   * The digest of the root, which must be an encoded
   * [Directory][google.devtools.remoteexecution.v1test.Directory] message
   * stored in the
   * [ContentAddressableStorage][google.devtools.remoteexecution.v1test.ContentAddressableStorage].
   * </pre>
   *
   * <code>.google.devtools.remoteexecution.v1test.Digest root_digest = 2;</code>
   */
  public com.google.devtools.remoteexecution.v1test.Digest getRootDigest() {
    return rootDigest_ == null ? com.google.devtools.remoteexecution.v1test.Digest.getDefaultInstance() : rootDigest_;
  }
  /**
   * <pre>
   * The digest of the root, which must be an encoded
   * [Directory][google.devtools.remoteexecution.v1test.Directory] message
   * stored in the
   * [ContentAddressableStorage][google.devtools.remoteexecution.v1test.ContentAddressableStorage].
   * </pre>
   *
   * <code>.google.devtools.remoteexecution.v1test.Digest root_digest = 2;</code>
   */
  public com.google.devtools.remoteexecution.v1test.DigestOrBuilder getRootDigestOrBuilder() {
    return getRootDigest();
  }

  public static final int PAGE_SIZE_FIELD_NUMBER = 3;
  private int pageSize_;
  /**
   * <pre>
   * A maximum page size to request. If present, the server will request no more
   * than this many items. Regardless of whether a page size is specified, the
   * server may place its own limit on the number of items to be returned and
   * require the client to retrieve more items using a subsequent request.
   * </pre>
   *
   * <code>int32 page_size = 3;</code>
   */
  public int getPageSize() {
    return pageSize_;
  }

  public static final int PAGE_TOKEN_FIELD_NUMBER = 4;
  private volatile java.lang.Object pageToken_;
  /**
   * <pre>
   * A page token, which must be a value received in a previous
   * [GetTreeResponse][google.devtools.remoteexecution.v1test.GetTreeResponse].
   * If present, the server will use it to return the following page of results.
   * </pre>
   *
   * <code>string page_token = 4;</code>
   */
  public java.lang.String getPageToken() {
    java.lang.Object ref = pageToken_;
    if (ref instanceof java.lang.String) {
      return (java.lang.String) ref;
    } else {
      com.google.protobuf.ByteString bs = 
          (com.google.protobuf.ByteString) ref;
      java.lang.String s = bs.toStringUtf8();
      pageToken_ = s;
      return s;
    }
  }
  /**
   * <pre>
   * A page token, which must be a value received in a previous
   * [GetTreeResponse][google.devtools.remoteexecution.v1test.GetTreeResponse].
   * If present, the server will use it to return the following page of results.
   * </pre>
   *
   * <code>string page_token = 4;</code>
   */
  public com.google.protobuf.ByteString
      getPageTokenBytes() {
    java.lang.Object ref = pageToken_;
    if (ref instanceof java.lang.String) {
      com.google.protobuf.ByteString b = 
          com.google.protobuf.ByteString.copyFromUtf8(
              (java.lang.String) ref);
      pageToken_ = b;
      return b;
    } else {
      return (com.google.protobuf.ByteString) ref;
    }
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (!getInstanceNameBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 1, instanceName_);
    }
    if (rootDigest_ != null) {
      output.writeMessage(2, getRootDigest());
    }
    if (pageSize_ != 0) {
      output.writeInt32(3, pageSize_);
    }
    if (!getPageTokenBytes().isEmpty()) {
      com.google.protobuf.GeneratedMessageV3.writeString(output, 4, pageToken_);
    }
    unknownFields.writeTo(output);
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (!getInstanceNameBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, instanceName_);
    }
    if (rootDigest_ != null) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, getRootDigest());
    }
    if (pageSize_ != 0) {
      size += com.google.protobuf.CodedOutputStream
        .computeInt32Size(3, pageSize_);
    }
    if (!getPageTokenBytes().isEmpty()) {
      size += com.google.protobuf.GeneratedMessageV3.computeStringSize(4, pageToken_);
    }
    size += unknownFields.getSerializedSize();
    memoizedSize = size;
    return size;
  }

  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof com.google.devtools.remoteexecution.v1test.GetTreeRequest)) {
      return super.equals(obj);
    }
    com.google.devtools.remoteexecution.v1test.GetTreeRequest other = (com.google.devtools.remoteexecution.v1test.GetTreeRequest) obj;

    boolean result = true;
    result = result && getInstanceName()
        .equals(other.getInstanceName());
    result = result && (hasRootDigest() == other.hasRootDigest());
    if (hasRootDigest()) {
      result = result && getRootDigest()
          .equals(other.getRootDigest());
    }
    result = result && (getPageSize()
        == other.getPageSize());
    result = result && getPageToken()
        .equals(other.getPageToken());
    result = result && unknownFields.equals(other.unknownFields);
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    hash = (37 * hash) + INSTANCE_NAME_FIELD_NUMBER;
    hash = (53 * hash) + getInstanceName().hashCode();
    if (hasRootDigest()) {
      hash = (37 * hash) + ROOT_DIGEST_FIELD_NUMBER;
      hash = (53 * hash) + getRootDigest().hashCode();
    }
    hash = (37 * hash) + PAGE_SIZE_FIELD_NUMBER;
    hash = (53 * hash) + getPageSize();
    hash = (37 * hash) + PAGE_TOKEN_FIELD_NUMBER;
    hash = (53 * hash) + getPageToken().hashCode();
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static com.google.devtools.remoteexecution.v1test.GetTreeRequest parseFrom(
      java.nio.ByteBuffer data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.devtools.remoteexecution.v1test.GetTreeRequest parseFrom(
      java.nio.ByteBuffer data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.devtools.remoteexecution.v1test.GetTreeRequest parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.devtools.remoteexecution.v1test.GetTreeRequest parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.devtools.remoteexecution.v1test.GetTreeRequest parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static com.google.devtools.remoteexecution.v1test.GetTreeRequest parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static com.google.devtools.remoteexecution.v1test.GetTreeRequest parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.google.devtools.remoteexecution.v1test.GetTreeRequest parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.google.devtools.remoteexecution.v1test.GetTreeRequest parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static com.google.devtools.remoteexecution.v1test.GetTreeRequest parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static com.google.devtools.remoteexecution.v1test.GetTreeRequest parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static com.google.devtools.remoteexecution.v1test.GetTreeRequest parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(com.google.devtools.remoteexecution.v1test.GetTreeRequest prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * <pre>
   * A request message for
   * [ContentAddressableStorage.GetTree][google.devtools.remoteexecution.v1test.ContentAddressableStorage.GetTree].
   * This message is deprecated and should no longer be used.
   * </pre>
   *
   * Protobuf type {@code google.devtools.remoteexecution.v1test.GetTreeRequest}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:google.devtools.remoteexecution.v1test.GetTreeRequest)
      com.google.devtools.remoteexecution.v1test.GetTreeRequestOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.google.devtools.remoteexecution.v1test.RemoteExecutionProto.internal_static_google_devtools_remoteexecution_v1test_GetTreeRequest_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.google.devtools.remoteexecution.v1test.RemoteExecutionProto.internal_static_google_devtools_remoteexecution_v1test_GetTreeRequest_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.google.devtools.remoteexecution.v1test.GetTreeRequest.class, com.google.devtools.remoteexecution.v1test.GetTreeRequest.Builder.class);
    }

    // Construct using com.google.devtools.remoteexecution.v1test.GetTreeRequest.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      instanceName_ = "";

      if (rootDigestBuilder_ == null) {
        rootDigest_ = null;
      } else {
        rootDigest_ = null;
        rootDigestBuilder_ = null;
      }
      pageSize_ = 0;

      pageToken_ = "";

      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return com.google.devtools.remoteexecution.v1test.RemoteExecutionProto.internal_static_google_devtools_remoteexecution_v1test_GetTreeRequest_descriptor;
    }

    public com.google.devtools.remoteexecution.v1test.GetTreeRequest getDefaultInstanceForType() {
      return com.google.devtools.remoteexecution.v1test.GetTreeRequest.getDefaultInstance();
    }

    public com.google.devtools.remoteexecution.v1test.GetTreeRequest build() {
      com.google.devtools.remoteexecution.v1test.GetTreeRequest result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public com.google.devtools.remoteexecution.v1test.GetTreeRequest buildPartial() {
      com.google.devtools.remoteexecution.v1test.GetTreeRequest result = new com.google.devtools.remoteexecution.v1test.GetTreeRequest(this);
      result.instanceName_ = instanceName_;
      if (rootDigestBuilder_ == null) {
        result.rootDigest_ = rootDigest_;
      } else {
        result.rootDigest_ = rootDigestBuilder_.build();
      }
      result.pageSize_ = pageSize_;
      result.pageToken_ = pageToken_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, java.lang.Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        java.lang.Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof com.google.devtools.remoteexecution.v1test.GetTreeRequest) {
        return mergeFrom((com.google.devtools.remoteexecution.v1test.GetTreeRequest)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(com.google.devtools.remoteexecution.v1test.GetTreeRequest other) {
      if (other == com.google.devtools.remoteexecution.v1test.GetTreeRequest.getDefaultInstance()) return this;
      if (!other.getInstanceName().isEmpty()) {
        instanceName_ = other.instanceName_;
        onChanged();
      }
      if (other.hasRootDigest()) {
        mergeRootDigest(other.getRootDigest());
      }
      if (other.getPageSize() != 0) {
        setPageSize(other.getPageSize());
      }
      if (!other.getPageToken().isEmpty()) {
        pageToken_ = other.pageToken_;
        onChanged();
      }
      this.mergeUnknownFields(other.unknownFields);
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      com.google.devtools.remoteexecution.v1test.GetTreeRequest parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (com.google.devtools.remoteexecution.v1test.GetTreeRequest) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }

    private java.lang.Object instanceName_ = "";
    /**
     * <pre>
     * The instance of the execution system to operate against. A server may
     * support multiple instances of the execution system (with their own workers,
     * storage, caches, etc.). The server MAY require use of this field to select
     * between them in an implementation-defined fashion, otherwise it can be
     * omitted.
     * </pre>
     *
     * <code>string instance_name = 1;</code>
     */
    public java.lang.String getInstanceName() {
      java.lang.Object ref = instanceName_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        instanceName_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * The instance of the execution system to operate against. A server may
     * support multiple instances of the execution system (with their own workers,
     * storage, caches, etc.). The server MAY require use of this field to select
     * between them in an implementation-defined fashion, otherwise it can be
     * omitted.
     * </pre>
     *
     * <code>string instance_name = 1;</code>
     */
    public com.google.protobuf.ByteString
        getInstanceNameBytes() {
      java.lang.Object ref = instanceName_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        instanceName_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * The instance of the execution system to operate against. A server may
     * support multiple instances of the execution system (with their own workers,
     * storage, caches, etc.). The server MAY require use of this field to select
     * between them in an implementation-defined fashion, otherwise it can be
     * omitted.
     * </pre>
     *
     * <code>string instance_name = 1;</code>
     */
    public Builder setInstanceName(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      instanceName_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * The instance of the execution system to operate against. A server may
     * support multiple instances of the execution system (with their own workers,
     * storage, caches, etc.). The server MAY require use of this field to select
     * between them in an implementation-defined fashion, otherwise it can be
     * omitted.
     * </pre>
     *
     * <code>string instance_name = 1;</code>
     */
    public Builder clearInstanceName() {
      
      instanceName_ = getDefaultInstance().getInstanceName();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * The instance of the execution system to operate against. A server may
     * support multiple instances of the execution system (with their own workers,
     * storage, caches, etc.). The server MAY require use of this field to select
     * between them in an implementation-defined fashion, otherwise it can be
     * omitted.
     * </pre>
     *
     * <code>string instance_name = 1;</code>
     */
    public Builder setInstanceNameBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      instanceName_ = value;
      onChanged();
      return this;
    }

    private com.google.devtools.remoteexecution.v1test.Digest rootDigest_ = null;
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.devtools.remoteexecution.v1test.Digest, com.google.devtools.remoteexecution.v1test.Digest.Builder, com.google.devtools.remoteexecution.v1test.DigestOrBuilder> rootDigestBuilder_;
    /**
     * <pre>
     * The digest of the root, which must be an encoded
     * [Directory][google.devtools.remoteexecution.v1test.Directory] message
     * stored in the
     * [ContentAddressableStorage][google.devtools.remoteexecution.v1test.ContentAddressableStorage].
     * </pre>
     *
     * <code>.google.devtools.remoteexecution.v1test.Digest root_digest = 2;</code>
     */
    public boolean hasRootDigest() {
      return rootDigestBuilder_ != null || rootDigest_ != null;
    }
    /**
     * <pre>
     * The digest of the root, which must be an encoded
     * [Directory][google.devtools.remoteexecution.v1test.Directory] message
     * stored in the
     * [ContentAddressableStorage][google.devtools.remoteexecution.v1test.ContentAddressableStorage].
     * </pre>
     *
     * <code>.google.devtools.remoteexecution.v1test.Digest root_digest = 2;</code>
     */
    public com.google.devtools.remoteexecution.v1test.Digest getRootDigest() {
      if (rootDigestBuilder_ == null) {
        return rootDigest_ == null ? com.google.devtools.remoteexecution.v1test.Digest.getDefaultInstance() : rootDigest_;
      } else {
        return rootDigestBuilder_.getMessage();
      }
    }
    /**
     * <pre>
     * The digest of the root, which must be an encoded
     * [Directory][google.devtools.remoteexecution.v1test.Directory] message
     * stored in the
     * [ContentAddressableStorage][google.devtools.remoteexecution.v1test.ContentAddressableStorage].
     * </pre>
     *
     * <code>.google.devtools.remoteexecution.v1test.Digest root_digest = 2;</code>
     */
    public Builder setRootDigest(com.google.devtools.remoteexecution.v1test.Digest value) {
      if (rootDigestBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        rootDigest_ = value;
        onChanged();
      } else {
        rootDigestBuilder_.setMessage(value);
      }

      return this;
    }
    /**
     * <pre>
     * The digest of the root, which must be an encoded
     * [Directory][google.devtools.remoteexecution.v1test.Directory] message
     * stored in the
     * [ContentAddressableStorage][google.devtools.remoteexecution.v1test.ContentAddressableStorage].
     * </pre>
     *
     * <code>.google.devtools.remoteexecution.v1test.Digest root_digest = 2;</code>
     */
    public Builder setRootDigest(
        com.google.devtools.remoteexecution.v1test.Digest.Builder builderForValue) {
      if (rootDigestBuilder_ == null) {
        rootDigest_ = builderForValue.build();
        onChanged();
      } else {
        rootDigestBuilder_.setMessage(builderForValue.build());
      }

      return this;
    }
    /**
     * <pre>
     * The digest of the root, which must be an encoded
     * [Directory][google.devtools.remoteexecution.v1test.Directory] message
     * stored in the
     * [ContentAddressableStorage][google.devtools.remoteexecution.v1test.ContentAddressableStorage].
     * </pre>
     *
     * <code>.google.devtools.remoteexecution.v1test.Digest root_digest = 2;</code>
     */
    public Builder mergeRootDigest(com.google.devtools.remoteexecution.v1test.Digest value) {
      if (rootDigestBuilder_ == null) {
        if (rootDigest_ != null) {
          rootDigest_ =
            com.google.devtools.remoteexecution.v1test.Digest.newBuilder(rootDigest_).mergeFrom(value).buildPartial();
        } else {
          rootDigest_ = value;
        }
        onChanged();
      } else {
        rootDigestBuilder_.mergeFrom(value);
      }

      return this;
    }
    /**
     * <pre>
     * The digest of the root, which must be an encoded
     * [Directory][google.devtools.remoteexecution.v1test.Directory] message
     * stored in the
     * [ContentAddressableStorage][google.devtools.remoteexecution.v1test.ContentAddressableStorage].
     * </pre>
     *
     * <code>.google.devtools.remoteexecution.v1test.Digest root_digest = 2;</code>
     */
    public Builder clearRootDigest() {
      if (rootDigestBuilder_ == null) {
        rootDigest_ = null;
        onChanged();
      } else {
        rootDigest_ = null;
        rootDigestBuilder_ = null;
      }

      return this;
    }
    /**
     * <pre>
     * The digest of the root, which must be an encoded
     * [Directory][google.devtools.remoteexecution.v1test.Directory] message
     * stored in the
     * [ContentAddressableStorage][google.devtools.remoteexecution.v1test.ContentAddressableStorage].
     * </pre>
     *
     * <code>.google.devtools.remoteexecution.v1test.Digest root_digest = 2;</code>
     */
    public com.google.devtools.remoteexecution.v1test.Digest.Builder getRootDigestBuilder() {
      
      onChanged();
      return getRootDigestFieldBuilder().getBuilder();
    }
    /**
     * <pre>
     * The digest of the root, which must be an encoded
     * [Directory][google.devtools.remoteexecution.v1test.Directory] message
     * stored in the
     * [ContentAddressableStorage][google.devtools.remoteexecution.v1test.ContentAddressableStorage].
     * </pre>
     *
     * <code>.google.devtools.remoteexecution.v1test.Digest root_digest = 2;</code>
     */
    public com.google.devtools.remoteexecution.v1test.DigestOrBuilder getRootDigestOrBuilder() {
      if (rootDigestBuilder_ != null) {
        return rootDigestBuilder_.getMessageOrBuilder();
      } else {
        return rootDigest_ == null ?
            com.google.devtools.remoteexecution.v1test.Digest.getDefaultInstance() : rootDigest_;
      }
    }
    /**
     * <pre>
     * The digest of the root, which must be an encoded
     * [Directory][google.devtools.remoteexecution.v1test.Directory] message
     * stored in the
     * [ContentAddressableStorage][google.devtools.remoteexecution.v1test.ContentAddressableStorage].
     * </pre>
     *
     * <code>.google.devtools.remoteexecution.v1test.Digest root_digest = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        com.google.devtools.remoteexecution.v1test.Digest, com.google.devtools.remoteexecution.v1test.Digest.Builder, com.google.devtools.remoteexecution.v1test.DigestOrBuilder> 
        getRootDigestFieldBuilder() {
      if (rootDigestBuilder_ == null) {
        rootDigestBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            com.google.devtools.remoteexecution.v1test.Digest, com.google.devtools.remoteexecution.v1test.Digest.Builder, com.google.devtools.remoteexecution.v1test.DigestOrBuilder>(
                getRootDigest(),
                getParentForChildren(),
                isClean());
        rootDigest_ = null;
      }
      return rootDigestBuilder_;
    }

    private int pageSize_ ;
    /**
     * <pre>
     * A maximum page size to request. If present, the server will request no more
     * than this many items. Regardless of whether a page size is specified, the
     * server may place its own limit on the number of items to be returned and
     * require the client to retrieve more items using a subsequent request.
     * </pre>
     *
     * <code>int32 page_size = 3;</code>
     */
    public int getPageSize() {
      return pageSize_;
    }
    /**
     * <pre>
     * A maximum page size to request. If present, the server will request no more
     * than this many items. Regardless of whether a page size is specified, the
     * server may place its own limit on the number of items to be returned and
     * require the client to retrieve more items using a subsequent request.
     * </pre>
     *
     * <code>int32 page_size = 3;</code>
     */
    public Builder setPageSize(int value) {
      
      pageSize_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * A maximum page size to request. If present, the server will request no more
     * than this many items. Regardless of whether a page size is specified, the
     * server may place its own limit on the number of items to be returned and
     * require the client to retrieve more items using a subsequent request.
     * </pre>
     *
     * <code>int32 page_size = 3;</code>
     */
    public Builder clearPageSize() {
      
      pageSize_ = 0;
      onChanged();
      return this;
    }

    private java.lang.Object pageToken_ = "";
    /**
     * <pre>
     * A page token, which must be a value received in a previous
     * [GetTreeResponse][google.devtools.remoteexecution.v1test.GetTreeResponse].
     * If present, the server will use it to return the following page of results.
     * </pre>
     *
     * <code>string page_token = 4;</code>
     */
    public java.lang.String getPageToken() {
      java.lang.Object ref = pageToken_;
      if (!(ref instanceof java.lang.String)) {
        com.google.protobuf.ByteString bs =
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        pageToken_ = s;
        return s;
      } else {
        return (java.lang.String) ref;
      }
    }
    /**
     * <pre>
     * A page token, which must be a value received in a previous
     * [GetTreeResponse][google.devtools.remoteexecution.v1test.GetTreeResponse].
     * If present, the server will use it to return the following page of results.
     * </pre>
     *
     * <code>string page_token = 4;</code>
     */
    public com.google.protobuf.ByteString
        getPageTokenBytes() {
      java.lang.Object ref = pageToken_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        pageToken_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }
    /**
     * <pre>
     * A page token, which must be a value received in a previous
     * [GetTreeResponse][google.devtools.remoteexecution.v1test.GetTreeResponse].
     * If present, the server will use it to return the following page of results.
     * </pre>
     *
     * <code>string page_token = 4;</code>
     */
    public Builder setPageToken(
        java.lang.String value) {
      if (value == null) {
    throw new NullPointerException();
  }
  
      pageToken_ = value;
      onChanged();
      return this;
    }
    /**
     * <pre>
     * A page token, which must be a value received in a previous
     * [GetTreeResponse][google.devtools.remoteexecution.v1test.GetTreeResponse].
     * If present, the server will use it to return the following page of results.
     * </pre>
     *
     * <code>string page_token = 4;</code>
     */
    public Builder clearPageToken() {
      
      pageToken_ = getDefaultInstance().getPageToken();
      onChanged();
      return this;
    }
    /**
     * <pre>
     * A page token, which must be a value received in a previous
     * [GetTreeResponse][google.devtools.remoteexecution.v1test.GetTreeResponse].
     * If present, the server will use it to return the following page of results.
     * </pre>
     *
     * <code>string page_token = 4;</code>
     */
    public Builder setPageTokenBytes(
        com.google.protobuf.ByteString value) {
      if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
      
      pageToken_ = value;
      onChanged();
      return this;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.setUnknownFieldsProto3(unknownFields);
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return super.mergeUnknownFields(unknownFields);
    }


    // @@protoc_insertion_point(builder_scope:google.devtools.remoteexecution.v1test.GetTreeRequest)
  }

  // @@protoc_insertion_point(class_scope:google.devtools.remoteexecution.v1test.GetTreeRequest)
  private static final com.google.devtools.remoteexecution.v1test.GetTreeRequest DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new com.google.devtools.remoteexecution.v1test.GetTreeRequest();
  }

  public static com.google.devtools.remoteexecution.v1test.GetTreeRequest getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<GetTreeRequest>
      PARSER = new com.google.protobuf.AbstractParser<GetTreeRequest>() {
    public GetTreeRequest parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new GetTreeRequest(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<GetTreeRequest> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<GetTreeRequest> getParserForType() {
    return PARSER;
  }

  public com.google.devtools.remoteexecution.v1test.GetTreeRequest getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

