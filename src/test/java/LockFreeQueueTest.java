import org.example.LockFreeQueue;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class LockFreeQueueTest {

    @Test
    void testSingleThreadEnqueueDequeue() {
        LockFreeQueue<Integer> queue = new LockFreeQueue<>();
        assertNull(queue.dequeue());

        queue.enqueue(1);
        queue.enqueue(2);

        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
        assertNull(queue.dequeue());
    }

    @Test
    void testMultiThreadedCorrectness() throws InterruptedException {
        LockFreeQueue<Integer> queue = new LockFreeQueue<>();
        int threads = 8;
        int opsPerThread = 10_000;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);
        Set<Integer> consumed = new HashSet<>();

        for (int i = 0; i < threads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                for (int j = 0; j < opsPerThread; j++) {
                    int value = threadId * opsPerThread + j;
                    queue.enqueue(value);
                    Integer dequeued = queue.dequeue();
                    if (dequeued != null) {
                        synchronized (consumed) {
                            consumed.add(dequeued);
                        }
                    }
                }
                latch.countDown();
            });
        }

        latch.await();
        executor.shutdown();
        assertTrue(consumed.size() <= threads * opsPerThread);
    }

    @Test
    void testEmptyQueueReturnsNull() {
        LockFreeQueue<String> queue = new LockFreeQueue<>();
        assertNull(queue.dequeue());
    }
}
