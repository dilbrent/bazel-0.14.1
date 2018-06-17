// Copyright 2018 The Bazel Authors. All rights reserved.
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

package com.google.devtools.build.lib.packages;

/** Dependencies of {@value Package#Codec}. */
public interface PackageCodecDependencies {

  PackageSerializerInterface getPackageSerializer();

  PackageDeserializerInterface getPackageDeserializer();

  /** Simplest implementation of PackageCodecDependencies. */
  public static class SimplePackageCodecDependencies implements PackageCodecDependencies {
    private final PackageSerializerInterface packageSerializer;
    private final PackageDeserializerInterface packageDeserializer;

    public SimplePackageCodecDependencies(
        PackageSerializerInterface packageSerializer,
        PackageDeserializerInterface packageDeserializer) {
      this.packageSerializer = packageSerializer;
      this.packageDeserializer = packageDeserializer;
    }

    @Override
    public PackageSerializerInterface getPackageSerializer() {
      return packageSerializer;
    }

    @Override
    public PackageDeserializerInterface getPackageDeserializer() {
      return packageDeserializer;
    }
  }
}
