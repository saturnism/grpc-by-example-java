package com.example.grpc.client;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.learn.GreetingServiceGrpc;
import com.example.learn.HelloRequest;
import com.example.learn.HelloResponse;
import com.example.learn.Sentiment;

import dmax.dialog.SpotsDialog;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

/**
 * :=  created by:  Shuza
 * :=  create date:  10-Jul-2018
 * :=  (C) CopyRight Shuza
 * :=  www.shuza.ninja
 * :=  shuza.sa@gmail.com
 * :=  Fun  :  Coffee  :  Code
 **/

public class MainActivity extends AppCompatActivity {

    private TextView tvResult;
    private Button btnConnect;

    private final String SERVER_ADDRESS = "192.168.43.169";
    private final int PORT = 8080;

    private SpotsDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();

        dialog = new SpotsDialog(this, R.style.LoadingDialog);
        dialog.setCancelable(false);

        btnConnect.setOnClickListener((View v) -> {
            ManagedChannel channel = ManagedChannelBuilder.forAddress(SERVER_ADDRESS, PORT)
                    .usePlaintext()
                    .build();

            requestExampleService(channel);
        });
    }

    private void requestExampleService(ManagedChannel channel) {
        GreetingServiceGrpc.GreetingServiceBlockingStub stub = GreetingServiceGrpc
                .newBlockingStub(channel);

        HelloRequest helloRequest = HelloRequest.newBuilder()
                .setName("Shuza")
                .setAge(20)
                .setSentiment(Sentiment.HAPPY)
                .build();

        dialog.show();

        Thread backgroundThread = new Thread(() -> {
            try {
                HelloResponse helloResponse = stub.greeting(helloRequest);
                showResultMessage("Response:  " + helloResponse.getGreeting());
            } catch (Exception e) {
                showResultMessage("Error:   " + e.getMessage());
            }
        });


        backgroundThread.start();

    }

    /**
     * show message on UI Thread
     * and dismiss loading dialog
     *
     * @param message
     */
    private void showResultMessage(String message) {
        runOnUiThread(() -> {
            tvResult.setText(message);
            dialog.dismiss();
        });
    }

    /**
     * find all components from view
     */
    private void findViews() {
        tvResult = findViewById(R.id.tv_result);
        btnConnect = findViewById(R.id.btn_server_connect);
    }
}
