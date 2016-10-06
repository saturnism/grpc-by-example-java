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

import com.auth0.jwt.JWTVerifier;
import com.example.grpc.Constant;
import io.grpc.*;

import java.util.Map;

/**
 * Created by rayt on 10/6/16.
 */
public class TraceIdServerInterceptor implements ServerInterceptor {
  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall, Metadata metadata, ServerCallHandler<ReqT, RespT> serverCallHandler) {
    String traceId = metadata.get(Constant.TRACE_ID_METADATA_KEY);
    Context ctx = Context.current().withValue(Constant.TRACE_ID_CTX_KEY, traceId);

    return Contexts.interceptCall(ctx, serverCall, metadata, serverCallHandler);
  }
}
