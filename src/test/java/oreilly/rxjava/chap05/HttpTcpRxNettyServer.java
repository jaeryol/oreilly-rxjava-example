package oreilly.rxjava.chap05;

import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.reactivex.netty.protocol.tcp.server.TcpServer;
import rx.Observable;

import java.nio.charset.StandardCharsets;

public class HttpTcpRxNettyServer {

    public static final Observable<String> RESPONSE = Observable.just(
        "HTTP/1.1 200 OK\r\n" +
            "Content-length: 2\r\n\r\n"
    );
    public static void main(String[] args) {
        TcpServer
            .newServer(8080)
            .<String, String>pipelineConfigurator(p -> {
                p.addLast(new LineBasedFrameDecoder(128));
                p.addLast(new StringDecoder(StandardCharsets.UTF_8));
            })
            .start(conn -> {
                Observable<String> output = conn
                    .getInput()
                    .flatMap(line -> {
                        if (line.isEmpty()) {
                            return RESPONSE;
                        } else {
                            return Observable.empty();
                        }
                    });
                return conn.writeAndFlushOnEach(output);
            })
            .awaitShutdown();
    }

}
