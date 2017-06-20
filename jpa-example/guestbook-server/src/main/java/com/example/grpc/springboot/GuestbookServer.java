package com.example.grpc.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Created by rayt on 5/17/16.
 */
@SpringBootApplication
public class GuestbookServer {
  public static void main(String[] args) {
    SpringApplication.run(GuestbookServer.class, args);
  }
}
