package org.example.jcstress;
import org.example.LockFreeQueue;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

@JCStressTest
@Description("Проверяет, что элементы возвращаются в порядке FIFO.")
@Outcome(id = "1, 2", expect = Expect.ACCEPTABLE)
@Outcome(id = "2, 1", expect = Expect.FORBIDDEN)
@Outcome(id = "0, 1", expect = Expect.ACCEPTABLE)
@Outcome(id = "1, 0", expect = Expect.ACCEPTABLE)
@Outcome(id = "0, 0", expect = Expect.ACCEPTABLE)
@State
public class FifoOrder {

    private final LockFreeQueue<Integer> q = new LockFreeQueue<>();

    @Actor
    public void p1() {
        q.enqueue(1);
        q.enqueue(2);
    }

    @Actor
    public void c1(II_Result r) {
        Integer v1 = q.dequeue();
        Integer v2 = q.dequeue();
        r.r1 = v1 == null ? 0 : v1;
        r.r2 = v2 == null ? 0 : v2;
    }
}

