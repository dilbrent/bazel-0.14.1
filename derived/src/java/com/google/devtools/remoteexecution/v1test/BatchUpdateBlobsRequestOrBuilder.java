// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/devtools/remoteexecution/v1test/remote_execution.proto

package com.google.devtools.remoteexecution.v1test;

public interface BatchUpdateBlobsRequestOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.devtools.remoteexecution.v1test.BatchUpdateBlobsRequest)
    com.google.protobuf.MessageOrBuilder {

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
  java.lang.String getInstanceName();
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
  com.google.protobuf.ByteString
      getInstanceNameBytes();

  /**
   * <pre>
   * The individual upload requests.
   * </pre>
   *
   * <code>repeated .google.devtools.remoteexecution.v1test.UpdateBlobRequest requests = 2;</code>
   */
  java.util.List<com.google.devtools.remoteexecution.v1test.UpdateBlobRequest> 
      getRequestsList();
  /**
   * <pre>
   * The individual upload requests.
   * </pre>
   *
   * <code>repeated .google.devtools.remoteexecution.v1test.UpdateBlobRequest requests = 2;</code>
   */
  com.google.devtools.remoteexecution.v1test.UpdateBlobRequest getRequests(int index);
  /**
   * <pre>
   * The individual upload requests.
   * </pre>
   *
   * <code>repeated .google.devtools.remoteexecution.v1test.UpdateBlobRequest requests = 2;</code>
   */
  int getRequestsCount();
  /**
   * <pre>
   * The individual upload requests.
   * </pre>
   *
   * <code>repeated .google.devtools.remoteexecution.v1test.UpdateBlobRequest requests = 2;</code>
   */
  java.util.List<? extends com.google.devtools.remoteexecution.v1test.UpdateBlobRequestOrBuilder> 
      getRequestsOrBuilderList();
  /**
   * <pre>
   * The individual upload requests.
   * </pre>
   *
   * <code>repeated .google.devtools.remoteexecution.v1test.UpdateBlobRequest requests = 2;</code>
   */
  com.google.devtools.remoteexecution.v1test.UpdateBlobRequestOrBuilder getRequestsOrBuilder(
      int index);
}
