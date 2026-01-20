package org.example.jcstress;

import org.example.LockFreeQueue;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;
import org.openjdk.jmh.annotations.Setup;

@JCStressTest
@Description("FIFO проверка - первый вошел первый вышел")
@Outcome(id = "1, 2", expect = Expect.ACCEPTABLE, desc = "правильно FIFO")
@Outcome(id = "2, 1", expect = Expect.FORBIDDEN, desc = "нарушение порядка!")
@Outcome(id = "0, 1", expect = Expect.ACCEPTABLE, desc = "первый null")
@Outcome(id = "1, 0", expect = Expect.ACCEPTABLE, desc = "второй null")
@Outcome(id = "0, 0", expect = Expect.ACCEPTABLE, desc = "пусто")
@State
public class FifoOrder {

    LockFreeQueue<Integer> queue;

    @Setup
    void init() {
        queue = new LockFreeQueue<>();
    }

    @Actor
    void producer() {
        queue.enqueue(1);
        queue.enqueue(2);
    }

    @Actor
    void reader(II_Result result) {
        Integer first = queue.dequeue();
        Integer second = queue.dequeue();

        result.r1 = first != null ? first : 0;
        result.r2 = second != null ? second : 0;
    }
}
