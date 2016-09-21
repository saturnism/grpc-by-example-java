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

package com.example.grpc.server;

import com.example.server.streaming.MetricsServiceGrpc;
import com.example.server.streaming.StreamingExample;
import io.grpc.stub.StreamObserver;

/**
 * Created by rayt on 5/16/16.
 */
public class MetricsServiceImpl extends MetricsServiceGrpc.MetricsServiceImplBase {
  @Override
  public StreamObserver<StreamingExample.Metric> collect(StreamObserver<StreamingExample.Average> responseObserver) {
    return new StreamObserver<StreamingExample.Metric>() {
      private long sum = 0;
      private long count = 0;

      @Override
      public void onNext(StreamingExample.Metric value) {
        System.out.println("value: " + value);
        sum += value.getMetric();
        count++;
      }

      @Override
      public void onError(Throwable t) {
        responseObserver.onError(t);
      }

      @Override
      public void onCompleted() {
        responseObserver.onNext(StreamingExample.Average.newBuilder()
            .setVal(sum / count)
            .build());
      }
    };
  }
}
