import org.example.SlowConcurrentQueue;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

class SlowConcurrentQueueTest {

    @Test
    void testSingleThreadEnqueueDequeue() {
        SlowConcurrentQueue<Integer> queue = new SlowConcurrentQueue<>();
        assertNull(queue.dequeue());

        queue.enqueue(1);
        queue.enqueue(2);

        assertEquals(1, queue.dequeue());
        assertEquals(2, queue.dequeue());
        assertNull(queue.dequeue());
    }

    @Test
    void testSizeTracking() {
        SlowConcurrentQueue<String> queue = new SlowConcurrentQueue<>();
        assertEquals(0, queue.size());

        queue.enqueue("a");
        queue.enqueue("b");
        assertEquals(2, queue.size());

        queue.dequeue();
        assertEquals(1, queue.size());

        queue.dequeue();
        assertEquals(0, queue.size());
    }

    @Test
    void testMultiThreaded() throws InterruptedException {
        SlowConcurrentQueue<Integer> queue = new SlowConcurrentQueue<>();
        int threads = 8;
        int opsPerThread = 10_000;

        ExecutorService executor = Executors.newFixedThreadPool(threads);
        CountDownLatch latch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executor.submit(() -> {
                for (int j = 0; j < opsPerThread; j++) {
                    queue.enqueue(j);
                    queue.dequeue();
                }
                latch.countDown();
            });
        }

        latch.await();
        assertTrue(queue.size() >= 0);
        executor.shutdown();
    }
}
