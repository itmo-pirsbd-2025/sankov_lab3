package org.example.jcstress;

import org.example.LockFreeQueue;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

@JCStressTest
@Outcome(id = "0, 0", expect = Expect.ACCEPTABLE, desc = "все consum'ы до enqueue")
@Outcome(id = "1, 0", expect = Expect.ACCEPTABLE, desc = "первый consumer взял")
@Outcome(id = "0, 1", expect = Expect.ACCEPTABLE, desc = "второй consumer взял")
@Outcome(id = "1, 1", expect = Expect.ACCEPTABLE, desc = "оба consum'а по элементу")
@Outcome(expect = Expect.FORBIDDEN, desc = "остальное - баг!")
@State
public class EnqueueDequeue {

    LockFreeQueue<Integer> queue = new LockFreeQueue<>();

    @Actor
    void actor1() {
        queue.enqueue(1);
    }

    @Actor
    void actor2() {
        queue.enqueue(2);
    }

    @Actor
    void consumer1(II_Result res) {
        Integer x = queue.dequeue();
        if (x != null) {
            res.r1 = 1;
        }
    }

    @Actor
    void consumer2(II_Result res) {
        Integer y = queue.dequeue();
        if (y != null) res.r2 = 1;
    }
}
