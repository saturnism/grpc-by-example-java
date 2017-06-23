/*
 * Copyright 2015 Google Inc. All Rights Reserved.
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
package com.example.guestbook;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by rayt on 5/1/17.
 */
@Configuration
public class ApplicationConfig {
  @Value("${guestbook.service.target}")
  private String guestbookServiceEndpoint;

  @Bean
  Channel channel() {
    return ManagedChannelBuilder.forTarget(guestbookServiceEndpoint)
        .usePlaintext(true)
        .build();
  }

  @Bean
  GuestbookServiceGrpc.GuestbookServiceBlockingStub guestbookServiceBlockingStub(Channel channel) {
    return GuestbookServiceGrpc.newBlockingStub(channel);
  }
}
