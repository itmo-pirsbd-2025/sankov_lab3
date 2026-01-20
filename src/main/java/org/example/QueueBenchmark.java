package org.example;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2)
@Measurement(iterations = 3)
@Fork(1)
@State(Scope.Thread)
public class QueueBenchmark {

    SlowConcurrentQueue<Integer> slowQ;
    LockFreeQueue<Integer> fastQ;

    @Param({"100000", "500000", "1000000"})
    int size;

    @Param({"4", "8", "16"})
    int nThreads;

    ExecutorService pool;

    @Setup public void up() {
        slowQ = new SlowConcurrentQueue<>();
        fastQ = new LockFreeQueue<>();
        pool = Executors.newFixedThreadPool(nThreads);
    }

    @TearDown public void down() {
        pool.shutdown();
    }

    void testQ(ConcurrentQueue<Integer> q) throws Exception {
        int prods = nThreads;
        int cons = nThreads;

        CountDownLatch done = new CountDownLatch(prods + cons);
        AtomicInteger sent = new AtomicInteger(), got = new AtomicInteger();

        for (int i = 0; i < prods; i++) {
            int id = i;
            pool.submit(() -> {
                for (int j = 0; j < size; j++) {
                    q.enqueue(id * size + j);
                    sent.getAndIncrement();
                }
                done.countDown();
            });
        }

        for (int i = 0; i < cons; i++) {
            pool.submit(() -> {
                while (got.get() < prods * size) {
                    Integer x = q.dequeue();
                    if (x != null) got.incrementAndGet();
                    else {
                        try {
                            Thread.sleep(1);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                done.countDown();
            });
        }

        done.await(30, TimeUnit.SECONDS);
    }

    @Benchmark
    public void slow() throws Exception {
        testQ(slowQ);
    }

    @Benchmark
    public void fast() throws Exception {
        testQ(fastQ);
    }

    public static void main(String[] ignore) throws Exception {
        new Runner(new OptionsBuilder()
                .include(QueueBenchmark.class.getSimpleName())
                .build()).run();
    }
}
