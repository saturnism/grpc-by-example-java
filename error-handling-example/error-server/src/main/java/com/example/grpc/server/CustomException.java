package com.example.grpc.server;

/**
 * Created by rayt on 6/24/17.
 */
public class CustomException extends Exception {
  public CustomException() {
  }

  public CustomException(String message) {
    super(message);
  }

  public CustomException(String message, Throwable cause) {
    super(message, cause);
  }

  public CustomException(Throwable cause) {
    super(cause);
  }

  public CustomException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
