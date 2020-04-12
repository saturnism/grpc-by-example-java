package com.exmaple.grpc.armeria;


import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.grpc.armeria.Hello.HelloReply;
import com.example.grpc.armeria.Hello.HelloRequest;
import com.example.grpc.armeria.HelloServiceGrpc.HelloServiceBlockingStub;
import com.example.grpc.armeria.HelloServiceGrpc.HelloServiceFutureStub;
import com.example.grpc.armeria.HelloServiceGrpc.HelloServiceStub;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;

import com.linecorp.armeria.client.Clients;

import io.grpc.stub.StreamObserver;

public final class ArmeriaGrpcClient {

    private static final Logger logger = LoggerFactory.getLogger(ArmeriaGrpcClient.class);

    public static void main(String[] args) throws InterruptedException {
        final String uri = "gproto+http://127.0.0.1:8080/";

        // Creates blocking gRPC client
        final HelloServiceBlockingStub blockingStub = Clients.newClient(uri, HelloServiceBlockingStub.class);
        final String blockingResponse = blockingStub.hello(HelloRequest.newBuilder().setName("Armeria").build())
                                           .getMessage();
        logger.info("Reply of 'hello': {}", blockingResponse);

        // Creates non-blocking gRPC client with Guava ListenableFuture
        final HelloServiceFutureStub futureStub = Clients.newClient(uri, HelloServiceFutureStub.class);
        final ListenableFuture<HelloReply> future =
                futureStub.lazyHello(HelloRequest.newBuilder().setName("Armeria").build());

        final CountDownLatch countDownLatch1 = new CountDownLatch(1);
        Futures.addCallback(future, new FutureCallback<HelloReply>() {
            @Override
            public void onSuccess(HelloReply result) {
                logger.info("Reply of 'lazyHello': {}", result.getMessage());
                countDownLatch1.countDown();
            }

            @Override
            public void onFailure(Throwable t) {
                throw new Error(t); // Should never reach here.
            }
        }, MoreExecutors.directExecutor());

        countDownLatch1.await();

        // Creates non-blocking gRPC client with StreamObserver
        final HelloServiceStub helloService = Clients.newClient(uri, HelloServiceStub.class);
        final CountDownLatch countDownLatch2 = new CountDownLatch(1);
        helloService.lotsOfReplies(
                HelloRequest.newBuilder().setName("Armeria").build(),
                new StreamObserver<HelloReply>() {

                    @Override
                    public void onNext(HelloReply value) {
                        logger.info("Reply of 'lotsOfReplies: {}", value.getMessage());
                    }

                    @Override
                    public void onError(Throwable t) {
                        throw new Error(t); // Should never reach here.
                    }

                    @Override
                    public void onCompleted() {
                        countDownLatch2.countDown();
                    }
                });
        countDownLatch2.await();
    }
}
