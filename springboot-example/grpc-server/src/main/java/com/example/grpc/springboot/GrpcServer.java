package com.example.grpc.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Created by rayt on 5/17/16.
 */
@SpringBootApplication
@EnableEurekaClient
public class GrpcServer {
  public static void main(String[] args) {
    SpringApplication.run(GrpcServer.class, args);
  }
}
