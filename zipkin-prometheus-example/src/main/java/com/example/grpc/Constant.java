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

package com.example.grpc;

import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.Sampler;
import io.grpc.Context;
import io.grpc.Metadata;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.urlconnection.URLConnectionSender;

import static io.grpc.Metadata.ASCII_STRING_MARSHALLER;

/**
 * Created by rayt on 10/6/16.
 */
public class Constant {
  public static Brave brave(String serviceName) {
    return new Brave.Builder(serviceName)
        .traceSampler(Sampler.ALWAYS_SAMPLE)
        .reporter(AsyncReporter.builder(URLConnectionSender.builder()
            .endpoint("http://docker-machine.dev:8080/api/v1/spans")
            .build()).build())
        .build();
  }
}
