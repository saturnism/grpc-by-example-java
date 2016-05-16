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

import com.example.server.GreetingRequest;
import com.example.server.GreetingResponse;
import com.example.server.GreetingServiceGrpc;
import com.example.server.Sentiment;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.SimpleLoadBalancerFactory;
import rx.internal.operators.BufferUntilSubscriber;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by rayt on 5/16/16.
 */
public class MyGrpcClient {
  public static void main(String[] args) throws InterruptedException {
    // 1. Channel, ManagedChannel, usePlainText?
    // 2. Load Balancing, Name Resolver
    // 3. Blocking vs Non-blocking Stubs, oh and Futures
    // 4. Builders
  }
}
