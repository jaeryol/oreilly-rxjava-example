package oreilly.rxjava.chap05;

import com.fasterxml.jackson.databind.util.ISO8601Utils;
import com.ning.http.client.AsyncCompletionHandler;
import com.ning.http.client.AsyncHttpClient;
import com.ning.http.client.Response;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlOutParameter;
import org.w3c.dom.Document;
import rx.Observable;
import rx.Single;
import rx.SingleSubscriber;
import rx.schedulers.Schedulers;

import java.io.IOException;

public class Chap05 {

    @Test
    public void single() {
        Single<String> single = Single.just("Hello, world!");
        single.subscribe(System.out::println);

        Single<Integer> error = Single.error(new RuntimeException(
            "Opps!!"
        ));
        error
            .observeOn(Schedulers.io())
            .subscribe(
                x -> System.out.println("x > " + x),
                Throwable::printStackTrace
            );
    }

    final AsyncHttpClient asyncHttpClient = new AsyncHttpClient();

    Single<Response> fetch(String address) {
        return Single.create(subscriber -> asyncHttpClient
            .prepareGet(address)
            .execute(handler(subscriber)));
    }

    AsyncCompletionHandler handler(
        SingleSubscriber<? super Response> subscriber
    ) {
        return new AsyncCompletionHandler() {
            @Override
            public Object onCompleted(Response response) throws Exception {
                subscriber.onSuccess(response);
                return response;
            }

            public void onThrowable(Throwable t) {
                subscriber.onError(t);
            }
        };
    }

    @Test
    public void example_fetch() {
        Single<String> example = fetch(
            "http://www.example.com"
        ).flatMap(this::body);
        String b = example.toBlocking().value();
    }

    Single<String> body(Response response) {
        return Single.create(subscriber -> {
            try {
                subscriber.onSuccess(response.getResponseBody());
            } catch (IOException e) {
                subscriber.onError(e);
            }
        });
    }

    Single<String> body2(Response response) {
        return Single.fromCallable(() -> response.getResponseBody());
    }

    Single<String> content(int id) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate();
        return Single.fromCallable(() ->
                jdbcTemplate.queryForObject(
                    "SELECT content FROM articles WHERE id = ?",
                    String.class,
                    id
                )
            ).subscribeOn(Schedulers.io());
    }

    Single<Integer> likes(int id) {
        return Single.just(10);
    }

    Single<Void> updateReadCount() {
        return Single.just(null);
    }
    Document buildHtml(String content, int likes) {
        //...
        return null;
    }

    public void doc_test() {
        Single<Document> doc = Single.zip(
            content(123),
            likes(123),
            updateReadCount(),
            (con, lks, vod) -> buildHtml(con, lks)
        );
    }

    @Test
    public void single_to_observable() {
        Single<String> single = Single.create(s -> {
            System.out.println("Subscribing");
            s.onSuccess("42");
        });

        Single<String> cachedSingle = single
            .toObservable()
            .cache()
            .toSingle();

        cachedSingle.subscribe(System.out::println);
        cachedSingle.subscribe(System.out::println);
    }

    @Test
    public void observable_to_single() {
        Single<Integer> emptySingle =
            Observable.<Integer>empty().toSingle();
        Single<Integer> doubleSingle =
            Observable.just(1, 2).toSingle();

        emptySingle.subscribe(); // throw exception msg -> Observable emitted no items
        doubleSingle.subscribe(); // throw exception msg -> Observable emitted too many elements
    }

    @Test
    public void observable_to_single_ignore() {
        Single<Integer> ignored = Single
            .just(1)
            .toObservable()
            .ignoreElements()
            .toSingle();
        ignored.subscribe();
    }
}
