package oreilly.rxjava.chap05;

import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;

import java.math.BigDecimal;

public class RestCurrencyServer{


    public static final BigDecimal RATE = new BigDecimal("1.06448");

    public static void main(String[] args) {
        HttpServer
            .newServer(8086)
            .start((req, res) -> {
                String amountStr = req.getDecodedPath().substring(1);
                BigDecimal amount = new BigDecimal(amountStr);
                Observable<String> response = Observable
                    .just(amount)
                    .map(eur -> eur.multiply(RATE))
                    .map(usd ->
                        "{ \"EUR\": " + amount + ", " + "\"USD\": " + usd + " } \n");
                return res.writeString(response);
            })
            .awaitShutdown();
    }
}
