package com.example.grpc.server;

import com.example.grpc.error.EchoRequest;
import com.example.grpc.error.EchoResponse;
import com.example.grpc.error.ErrorServiceGrpc;
import io.grpc.Context;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by rayt on 6/24/17.
 */
public class ErrorServiceImpl extends ErrorServiceGrpc.ErrorServiceImplBase {
  private static final Logger logger = Logger.getLogger(ErrorServiceImpl.class.getName());
  private static final ExecutorService CANCELLATION_EXECUTOR = Executors.newCachedThreadPool();
  private static final int SECONDS_TO_WAIT = 5;

  @Override
  public void customUnwrapException(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
    responseObserver.onError(new CustomException());
  }

  @Override
  public void customException(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
    try {
      throw new CustomException("Custom exception!");
    } catch (Exception e) {
      responseObserver.onError(Status.INTERNAL
          .withDescription(e.getMessage())
          .augmentDescription("customException()")
          .withCause(e) // This can be attached to the Status locally, but NOT transmitted to the client!
          .asRuntimeException());
    }
  }

  @Override
  public void uncaughtExceptions(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
    throw new NullPointerException("uncaughtExceptions(): Oops, not caught! What happes in the client?");
  }

  @Override
  public void automaticallyWrappedException(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
    responseObserver.onError(new IllegalArgumentException("This exception message and the stacktrace should automatically propagate to the client"));
  }

  @Override
  public void deadlineExceeded(EchoRequest request, StreamObserver<EchoResponse> responseObserver) {
    Context context = Context.current();

    context.addListener(new Context.CancellationListener() {
      @Override
      public void cancelled(Context context) {
        // CancellationCause is TimeoutException if it was exceeding the deadline
        logger.log(Level.INFO, "deadlineExceeded(): The call was cancelled.", context.cancellationCause());
      }
    }, CANCELLATION_EXECUTOR);

    context.run(() -> {
      int secondsElapsed = 0;
      while (secondsElapsed < SECONDS_TO_WAIT && !context.isCancelled()) {
        try {
          Thread.sleep(1000L);
        } catch (InterruptedException e) {
        }
        secondsElapsed++;
      }
      logger.log(Level.INFO, "deadlineExceeded(): The call ended after ~" + secondsElapsed + " seconds");
    });
  }
}
