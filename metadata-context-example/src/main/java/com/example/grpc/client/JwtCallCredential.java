/*
 * Copyright 2016 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.grpc.client;

import io.grpc.*;

import java.util.concurrent.Executor;

import static io.grpc.CallCredentials.ATTR_SECURITY_LEVEL;

/**
 * Created by rayt on 10/6/16.
 */
public class JwtCallCredential implements CallCredentials {
  private final String jwt;

  public JwtCallCredential(String jwt) {
    this.jwt = jwt;
  }

  @Override
  public void applyRequestMetadata(MethodDescriptor<?, ?> methodDescriptor, Attributes attributes, Executor executor, MetadataApplier metadataApplier) {
    String authority = attributes.get(ATTR_AUTHORITY);
    System.out.println(authority);
    executor.execute(new Runnable() {
      @Override
      public void run() {
        try {
          Metadata headers = new Metadata();
          Metadata.Key<String> jwtKey = Metadata.Key.of("jwt", Metadata.ASCII_STRING_MARSHALLER);
          headers.put(jwtKey, jwt);
          metadataApplier.apply(headers);
        } catch (Throwable e) {
          metadataApplier.fail(Status.UNAUTHENTICATED.withCause(e));
        }
      }
    });
  }

  @Override public void thisUsesUnstableApi() {
  }
}
