package oreilly.rxjava.chap05;

import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_LENGTH;

public class RxNettyHttpServer {

    public static final Observable<String> RESPONSE_OK = Observable.just(
        "OK\n"
    );

    public static void main(String[] args) {
        HttpServer
            .newServer(8086)
            .start((req, res) ->
                res
                    .setHeader(CONTENT_LENGTH, 2)
                    .writeStringAndFlushOnEach(RESPONSE_OK)
            ).awaitShutdown();
    }
}
