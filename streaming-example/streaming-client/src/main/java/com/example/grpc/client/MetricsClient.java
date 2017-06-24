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

import com.example.server.streaming.MetricsServiceGrpc;
import com.example.server.streaming.StreamingExample;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.StreamObserver;

import java.util.stream.Stream;

/**
 * Created by rayt on 5/16/16.
 */
public class MetricsClient {
  public static void main(String[] args) throws InterruptedException {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext(true).build();
    MetricsServiceGrpc.MetricsServiceStub stub = MetricsServiceGrpc.newStub(channel);

    StreamObserver<StreamingExample.Metric> collect = stub.collect(new StreamObserver<StreamingExample.Average>() {
      @Override
      public void onNext(StreamingExample.Average value) {
        System.out.println("Average: " + value.getVal());
      }

      @Override
      public void onError(Throwable t) {

      }

      @Override
      public void onCompleted() {

      }
    });

    Stream.of(1L, 2L, 3L, 4L).map(l -> StreamingExample.Metric.newBuilder().setMetric(l).build())
        .forEach(collect::onNext);
    collect.onCompleted();

    //channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
  }
}
