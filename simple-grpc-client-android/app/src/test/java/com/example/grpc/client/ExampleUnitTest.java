package com.example.grpc.client;

import com.example.learn.GreetingServiceGrpc;
import com.example.learn.HelloRequest;
import com.example.learn.HelloResponse;
import com.example.learn.Sentiment;

import org.junit.Test;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.okhttp.OkHttpChannelBuilder;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGRPC() {
        final String SERVER_ADDRESS = "192.168.43.169";
        final int PORT = 8080;

        ManagedChannel channel = OkHttpChannelBuilder.forAddress(SERVER_ADDRESS, PORT)
                .usePlaintext()
                .build();

        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc
                .newBlockingStub(channel);

        HelloRequest helloRequest = HelloRequest.newBuilder()
                .setName("Shuza")
                .setAge(20)
                .setSentiment(Sentiment.HAPPY)
                .build();

        HelloResponse helloResponse = stub.greeting(helloRequest);

        System.out.println("Result :    " + helloResponse.getGreeting());
        assertEquals("Hello there, Shuza", helloResponse.getGreeting());
    }
}