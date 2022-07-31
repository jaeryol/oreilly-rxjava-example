package oreilly.rxjava.chap06;

import org.junit.Test;
import rx.Observable;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static rx.Observable.just;

public class Chapter06 {

    @Test
    public void example_1() throws InterruptedException {
        long startTime = System.currentTimeMillis();
        Observable
            .interval(7, MILLISECONDS)
            .timestamp()
            .sample(1, TimeUnit.SECONDS)
            .map(ts -> ts.getTimestampMillis() - startTime + "ms: " + ts.getValue())
            .take(5)
            .subscribe(System.out::println);

        Thread.sleep(5000);
    }

    @Test
    public void example_2() throws InterruptedException {
        Observable<String> names = just(
                "Mary", "Patricia", "Linda",
                "Barbara", "Elizabeth", "Jennifer", "Maria",
                "Susan", "Margaret", "Dorothy"
            );

        Observable<Long> absoluteDelayMillis = just(
                0.1, 0.6, 0.9,
                1.1, 3.3, 3.4, 3.5, 3.6,
                4.4, 4.8
            )
            .map(d -> {
                System.out.println("dd > " + d);
                return (long) (d * 1_000);
            });

        Observable<String> delayedNames = names
            .zipWith(
                absoluteDelayMillis,
                (n, d) ->
                {
                    System.out.println("n > " + n + ", " + "d -> " + d);
                    return just(n).delay(d, MILLISECONDS);
                }
            ).flatMap(o -> o);

        delayedNames
            .sample(1, SECONDS)
            .subscribe(System.out::println);
        Thread.sleep(10000);
    }

    @Test
    public void example_3() {
        Observable
            .range(1, 7)
            .buffer(3)
            .subscribe(System.out::println);
    }

    @Test
    public void example_4() {
        Random random = new Random();
        Observable
            .defer(() -> just(random.nextGaussian()))
            .repeat(1000)
            .buffer(100, 1)
            .map(list -> {
                System.out.println("call this");
                return averageOfList(list);
            })
            .subscribe(System.out::println);
    }

    private double averageOfList(List<Double> list) {
        return list
            .stream()
            .collect(Collectors.averagingDouble(x -> x));
    }

    @Test
    public void example_5() {
        Observable<Integer> odd = Observable
            .range(1, 7)
            .buffer(1, 2)
            .flatMapIterable(s -> s);
        odd.subscribe(System.out::println);
    }
}
