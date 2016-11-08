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

import com.auth0.jwt.JWTSigner;
import com.example.grpc.*;
import com.example.grpc.server.TraceIdClientInterceptor;
import io.grpc.Context;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.HashMap;

/**
 * Created by rayt on 10/6/16.
 */
public class AuthClient {
  public static void main(String[] args) {
    String jwt = createJwt(Constant.JWT_SECRET, "authClient", "rayt");
    //String jwt = createJwt("123124123", "authClient", "rayt");
    System.out.println("Created a JWT: " + jwt);
    JwtCallCredential callCredential = new JwtCallCredential(jwt);

    ManagedChannel greetingChannel = ManagedChannelBuilder.forAddress("localhost", 8080)
        .usePlaintext(true)
        .intercept(new TraceIdClientInterceptor())
        .build();

    ManagedChannel goodbyeChannel = ManagedChannelBuilder.forAddress("localhost", 9090)
        .usePlaintext(true)
        .intercept(new TraceIdClientInterceptor())
        .build();

    Context.current().withValue(Constant.TRACE_ID_CTX_KEY, "1").run(() -> {
      GreetingServiceGrpc.GreetingServiceBlockingStub greetingStub = GreetingServiceGrpc.newBlockingStub(greetingChannel).withCallCredentials(callCredential);
      HelloResponse helloResponse = greetingStub.greeting(HelloRequest.newBuilder().setName("Ray").build());
      System.out.println(helloResponse);
    });

    Context.current().withValue(Constant.TRACE_ID_CTX_KEY, "2").run(() -> {
      GoodbyeServiceGrpc.GoodbyeServiceBlockingStub goodbyeStub = GoodbyeServiceGrpc.newBlockingStub(goodbyeChannel).withCallCredentials(callCredential);
      GoodbyeResponse goodbyeResponse = goodbyeStub.goodbye(GoodbyeRequest.newBuilder().setName("Jason").build());
      System.out.println(goodbyeResponse);
    });
  }

  public static String createJwt(String secret, String issuer, String subject) {
    final long iat = System.currentTimeMillis() / 1000l; // issued at claim
    final long exp = iat + 60L; // expires claim. In this case the token expires in 60 seconds

    final JWTSigner signer = new JWTSigner(secret);
    final HashMap<String, Object> claims = new HashMap<String, Object>();
    claims.put("iss", issuer);
    claims.put("exp", exp);
    claims.put("iat", iat);
    claims.put("sub", subject);

    return signer.sign(claims);
  }
}
