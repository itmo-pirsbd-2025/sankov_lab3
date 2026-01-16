package org.example.jcstress;

import org.example.ConcurrentQueue;
import org.example.LockFreeQueue;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

@JCStressTest
@Description("Проверяет, что один и тот же элемент не может быть возвращён двум потокам одновременно.")
@Outcome(id = "1, 0", expect = Expect.ACCEPTABLE)
@Outcome(id = "0, 1", expect = Expect.ACCEPTABLE)
@Outcome(id = "0, 0", expect = Expect.ACCEPTABLE)
@Outcome(id = "1, 1", expect = Expect.FORBIDDEN)
@State
public class NoDuplication {

    private final LockFreeQueue<Integer> q = new LockFreeQueue<>();

    @Actor
    public void p1() {
        q.enqueue(1);
    }

    @Actor
    public void c1(II_Result r) {
        Integer v = q.dequeue();
        r.r1 = v == null ? 0 : 1;
    }

    @Actor
    public void c2(II_Result r) {
        Integer v = q.dequeue();
        r.r2 = v == null ? 0 : 1;
    }
}

