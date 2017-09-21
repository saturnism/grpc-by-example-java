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

import com.example.server.streaming.RxMetricsServiceGrpc;
import com.example.server.streaming.Streaming;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.reactivex.Flowable;

import java.util.concurrent.ExecutionException;

/**
 * Created by rayt on 5/16/16.
 */
public class RxMetricsClient {
  public static void main(String[] args) throws InterruptedException, ExecutionException {
    ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 8080).usePlaintext(true).build();

    RxMetricsServiceGrpc.RxMetricsServiceStub rxStub = RxMetricsServiceGrpc.newRxStub(channel);

    rxStub.collect(Flowable.fromArray(1,2,3,4,5)
        .map(m -> Streaming.Metric.newBuilder().setMetric(m).build()))
        .subscribe(System.out::println);
  }
}
