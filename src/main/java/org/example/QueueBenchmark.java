package org.example;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@Warmup(iterations = 2, time = 1)
@Measurement(iterations = 3, time = 1)
@State(Scope.Thread)
@Fork(value = 1)
public class QueueBenchmark {

    private SlowConcurrentQueue<Integer> slowQueue;
    private LockFreeQueue<Integer> lockFreeQueue;

    @Param({"100000", "500000", "1000000"})
    private int opsPerProducer;

    @Param({"4", "8", "16"})
    private int threads;

    private ExecutorService executor;

    @Setup(Level.Invocation)
    public void setUp() {
        slowQueue = new SlowConcurrentQueue<>();
        lockFreeQueue = new LockFreeQueue<>();
        executor = Executors.newFixedThreadPool(threads * 2);
    }

    @TearDown(Level.Invocation)
    public void tearDown() {
        executor.shutdownNow();
    }

    private void runQueueBenchmark(ConcurrentQueue<Integer> queue) throws InterruptedException {
        int producersCount = threads;
        int consumersCount = threads;
        CountDownLatch latch = new CountDownLatch(producersCount + consumersCount);
        AtomicInteger producedCounter = new AtomicInteger();
        AtomicInteger consumedCounter = new AtomicInteger();

        // Producers
        for (int i = 0; i < producersCount; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < opsPerProducer; j++) {
                    queue.enqueue(threadId * opsPerProducer + j);
                    producedCounter.incrementAndGet();
                }
                latch.countDown();
            });
        }

        // Consumers
        for (int i = 0; i < consumersCount; i++) {
            executor.submit(() -> {
                int localConsumed = 0;
                while (consumedCounter.get() < producersCount * opsPerProducer) {
                    Integer val = queue.dequeue();
                    if (val != null) {
                        consumedCounter.incrementAndGet();
                        localConsumed++;
                    }
                }
                latch.countDown();
            });
        }

        latch.await();
    }

    @Benchmark
    public void benchmark1_slowQueue() throws InterruptedException {
        runQueueBenchmark(slowQueue);
    }

    @Benchmark
    public void benchmark2_lockFreeQueue() throws InterruptedException {
        runQueueBenchmark(lockFreeQueue);
    }

    public static void main(String[] args) throws Exception {
        Options opt = new OptionsBuilder()
                .include(QueueBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(2)
                .measurementIterations(3)
                .build();

        new Runner(opt).run();
    }
}
