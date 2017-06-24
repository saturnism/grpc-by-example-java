package com.example.grpc.server;

import io.grpc.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by rayt on 6/24/17.
 */
public class UnknownStatusDescriptionInterceptor implements ServerInterceptor {
  // Only Throwable classes listed here will be processed by the interceptor.
  // The interceptor will copy the cause's message & stacktrace into Status' description.
  private final Set<Class<? extends Throwable>> autowrapThrowables = new HashSet<>();

  public UnknownStatusDescriptionInterceptor(Collection<Class<? extends Throwable>> autowrapThrowables) {
    this.autowrapThrowables.addAll(autowrapThrowables);
  }

  @Override
  public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> call, Metadata headers, ServerCallHandler<ReqT, RespT> next) {
    ServerCall<ReqT, RespT> wrappedCall = new ForwardingServerCall.SimpleForwardingServerCall<ReqT, RespT>(call) {
      @Override
      public void sendMessage(RespT message) {
        super.sendMessage(message);
      }

      @Override
      public void close(Status status, Metadata trailers) {
        System.out.println("Interceptor: " + (status.getCause() == null ? "null" : status.getCause().getClass().getName()));
        if (status.getCode() == Status.Code.UNKNOWN
            && status.getDescription() == null
            && status.getCause() != null
            && autowrapThrowables.contains(status.getCause().getClass())) {
          Throwable e = status.getCause();
          status = Status.INTERNAL
              .withDescription(e.getMessage())
              .augmentDescription(stacktraceToString(e));
        }
        super.close(status, trailers);
      }
    };

    return next.startCall(wrappedCall, headers);
  }

  private String stacktraceToString(Throwable e) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    e.printStackTrace(printWriter);
    return stringWriter.toString();
  }
}
