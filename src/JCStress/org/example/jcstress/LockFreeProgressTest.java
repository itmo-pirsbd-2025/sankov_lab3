package org.example.jcstress;

import org.example.LockFreeQueue;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

@JCStressTest
@Outcome(id = "0, 0", expect = Expect.FORBIDDEN, desc = "Оба потока не смогли продвинуться")
@Outcome(expect = Expect.ACCEPTABLE, desc = "Хотя бы один поток делает прогресс")
@State
public class LockFreeProgressTest {

    private final LockFreeQueue<Integer> q = new LockFreeQueue<>();

    @Actor
    public void producer(II_Result r) {
        int success = 0;
        for (int i = 0; i < 1000; i++) {
            q.enqueue(i);
            success++;
        }
        r.r1 = success;
    }

    @Actor
    public void consumer(II_Result r) {
        int success = 0;
        for (int i = 0; i < 1000; i++) {
            Integer v = q.dequeue();
            if (v != null) {
                success++;
            }
        }
        r.r2 = success;
    }
}
