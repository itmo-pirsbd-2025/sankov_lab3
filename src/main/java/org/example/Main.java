package org.example;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    private static final int PRODUCERS = 8;
    private static final int CONSUMERS = 8;
    private static final int OPS_PER_PRODUCER = 10_000_000;


    public static void main(String[] args) throws Exception {
        System.out.println("\n=== SlowConcurrentQueue ===");
        long slowTime = runTest(new SlowConcurrentQueue<Integer>());
        System.out.println("Time: " + slowTime + " ms");
        System.out.println("\n=== LockFreeQueue ===");
        long lockFreeTime = runTest(new LockFreeQueue<Integer>());
        System.out.println("Time: " + lockFreeTime + " ms");
        System.out.println("\nSpeedup: x" +
                String.format("%.2f", (double) slowTime / lockFreeTime));
    }

    private static long runTest(ConcurrentQueue<Integer> queue) throws Exception {
        ExecutorService pool =
                Executors.newFixedThreadPool(PRODUCERS + CONSUMERS);

        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done =
                new CountDownLatch(PRODUCERS + CONSUMERS);

        for (int i = 0; i < PRODUCERS; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    for (int j = 0; j < OPS_PER_PRODUCER; j++) {
                        queue.enqueue(j);
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    done.countDown();
                }
            });
        }

        for (int i = 0; i < CONSUMERS; i++) {
            pool.submit(() -> {
                try {
                    start.await();
                    int taken = 0;
                    int target = OPS_PER_PRODUCER * PRODUCERS / CONSUMERS;

                    while (taken < target) {
                        Integer v = queue.dequeue();
                        if (v != null) {
                            taken++;
                        }
                    }
                } catch (InterruptedException ignored) {
                } finally {
                    done.countDown();
                }
            });
        }

        long startTime = System.currentTimeMillis();
        start.countDown();
        done.await();
        long endTime = System.currentTimeMillis();

        pool.shutdown();
        return endTime - startTime;
    }
}

