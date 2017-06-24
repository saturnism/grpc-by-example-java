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
import com.sun.javafx.font.Metrics;
import io.grpc.stub.StreamObserver;
import rx.Observable;
import rx.Subscriber;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by rayt on 11/2/16.
 * Thanks Mark (@mp911de) for helping out on this example!
 */
public class AverageOnSubscribe implements Observable.OnSubscribe<StreamingExample.Average> {
  private final MetricsServiceGrpc.MetricsServiceStub stub;
  private final Observable<StreamingExample.Metric> metrics;

  public AverageOnSubscribe(Observable<StreamingExample.Metric> metrics, MetricsServiceGrpc.MetricsServiceStub stub) {
    this.stub = stub;
    this.metrics = metrics;
  }

  @Override
  public void call(Subscriber<? super StreamingExample.Average> subscriber) {
    final AtomicBoolean started = new AtomicBoolean(false);
    StreamObserver<StreamingExample.Metric> toServer = stub.collect(new StreamObserver<StreamingExample.Average>() {
      @Override
      public void onNext(StreamingExample.Average average) {
        if (started.compareAndSet(false, true)) {
          subscriber.onStart();
        }
        subscriber.onNext(average);
      }

      @Override
      public void onError(Throwable throwable) {
        subscriber.onError(throwable);
      }

      @Override
      public void onCompleted() {
        try {
          subscriber.onCompleted();
        } catch (Exception e) {
          // catch this
        }
      }
    });

    metrics.forEach(toServer::onNext);
    toServer.onCompleted();
  }
}
