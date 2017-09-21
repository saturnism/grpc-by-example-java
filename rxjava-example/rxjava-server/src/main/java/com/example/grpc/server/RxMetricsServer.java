package com.example.grpc.server;

import com.example.server.streaming.RxMetricsServiceGrpc;
import com.example.server.streaming.Streaming;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.reactivex.Flowable;
import io.reactivex.Single;

import java.io.IOException;


public class RxMetricsServer {
  public static void main(String[] args) throws IOException, InterruptedException {
    RxMetricsServiceGrpc.MetricsServiceImplBase service = new RxMetricsServiceGrpc.MetricsServiceImplBase() {

      @Override
      public Single<Streaming.Average> collect(Flowable<Streaming.Metric> request) {
        return request.map(m -> m.getMetric())
            .map(m -> new State(m, 1))
            .reduce((a, b) -> new State(a.sum + b.sum, a.count + b.count))
            .map(s -> Streaming.Average.newBuilder().setVal(s.sum / s.count).build())
            .toSingle();
      }
    };

    Server server = ServerBuilder.forPort(8080)
        .addService(service)
        .build();

    server.start();
    server.awaitTermination();
  }

  static class State {
    protected long sum = 0;
    protected long count = 0;

    public State(long sum, long count) {
      this.sum = sum;
      this.count = count;
    }
  }
}
