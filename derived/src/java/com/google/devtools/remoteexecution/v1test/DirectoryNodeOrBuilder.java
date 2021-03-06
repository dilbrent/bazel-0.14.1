// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: google/devtools/remoteexecution/v1test/remote_execution.proto

package com.google.devtools.remoteexecution.v1test;

public interface DirectoryNodeOrBuilder extends
    // @@protoc_insertion_point(interface_extends:google.devtools.remoteexecution.v1test.DirectoryNode)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <pre>
   * The name of the directory.
   * </pre>
   *
   * <code>string name = 1;</code>
   */
  java.lang.String getName();
  /**
   * <pre>
   * The name of the directory.
   * </pre>
   *
   * <code>string name = 1;</code>
   */
  com.google.protobuf.ByteString
      getNameBytes();

  /**
   * <pre>
   * The digest of the
   * [Directory][google.devtools.remoteexecution.v1test.Directory] object
   * represented. See [Digest][google.devtools.remoteexecution.v1test.Digest]
   * for information about how to take the digest of a proto message.
   * </pre>
   *
   * <code>.google.devtools.remoteexecution.v1test.Digest digest = 2;</code>
   */
  boolean hasDigest();
  /**
   * <pre>
   * The digest of the
   * [Directory][google.devtools.remoteexecution.v1test.Directory] object
   * represented. See [Digest][google.devtools.remoteexecution.v1test.Digest]
   * for information about how to take the digest of a proto message.
   * </pre>
   *
   * <code>.google.devtools.remoteexecution.v1test.Digest digest = 2;</code>
   */
  com.google.devtools.remoteexecution.v1test.Digest getDigest();
  /**
   * <pre>
   * The digest of the
   * [Directory][google.devtools.remoteexecution.v1test.Directory] object
   * represented. See [Digest][google.devtools.remoteexecution.v1test.Digest]
   * for information about how to take the digest of a proto message.
   * </pre>
   *
   * <code>.google.devtools.remoteexecution.v1test.Digest digest = 2;</code>
   */
  com.google.devtools.remoteexecution.v1test.DigestOrBuilder getDigestOrBuilder();
}
