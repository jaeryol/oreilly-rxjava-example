package oreilly.rxjava.chap05;

import io.netty.buffer.ByteBuf;
import io.reactivex.netty.protocol.http.client.HttpClient;
import io.reactivex.netty.protocol.http.client.HttpClientResponse;
import org.junit.Test;
import rx.Observable;
import rx.Single;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RxJavaHttp {

    @Test
    public void run() throws Exception {
        Observable<ByteBuf> response = HttpClient
            .newClient("example.com", 80)
            .createGet("/")
            .flatMap(HttpClientResponse::getContent);
        response
            .map(bb -> bb.toString(UTF_8))
            .subscribe(System.out::println);
    }
}
