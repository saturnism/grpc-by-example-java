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

import com.example.grpc.Constant;
import com.example.grpc.GoodbyeRequest;
import com.example.grpc.GoodbyeResponse;
import com.example.grpc.GoodbyeServiceGrpc;
import com.github.kristofa.brave.grpc.BraveGrpcClientInterceptor;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import me.dinowernli.grpc.prometheus.Configuration;
import me.dinowernli.grpc.prometheus.MonitoringClientInterceptor;

/**
 * Created by rayt on 10/6/16.
 */
public class ZipkinExampleClient {
  public static void main(String[] args) {
    ManagedChannel goodbyeChannel = ManagedChannelBuilder.forAddress("localhost", 9090)
        .usePlaintext(true)
        .intercept(new BraveGrpcClientInterceptor(Constant.brave("client-example")))
        .intercept(MonitoringClientInterceptor.create(Configuration.allMetrics()))
        .build();

    for (int i = 0; i < 100; i++) {
      GoodbyeServiceGrpc.GoodbyeServiceBlockingStub stub = GoodbyeServiceGrpc.newBlockingStub(goodbyeChannel);
      GoodbyeResponse goodbye = stub.goodbye(GoodbyeRequest.newBuilder().setName("Ray").build());
      System.out.println(goodbye);
    }
    goodbyeChannel.shutdownNow();
  }
}
