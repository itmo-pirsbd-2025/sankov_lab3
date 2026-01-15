package org.example.jcstress;

import org.example.ConcurrentQueue;
import org.example.LockFreeQueue;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

@JCStressTest
@Outcome(id = "0, 0", expect = Expect.ACCEPTABLE,
        desc = "Оба consumer'а выполнились до enqueue.")
@Outcome(id = "1, 0", expect = Expect.ACCEPTABLE,
        desc = "Только один consumer получил элемент.")
@Outcome(id = "0, 1", expect = Expect.ACCEPTABLE,
        desc = "Только один consumer получил элемент.")
@Outcome(id = "1, 1", expect = Expect.ACCEPTABLE,
        desc = "Оба consumer'а получили элементы.")
@Outcome(expect = Expect.FORBIDDEN,
        desc = "Любое другое состояние недопустимо.")
@State
public class EnqueueDequeue {

    private final LockFreeQueue<Integer> q = new LockFreeQueue<>();

    @Actor
    public void p1() {
        q.enqueue(1);
    }

    @Actor
    public void p2() {
        q.enqueue(2);
    }

    @Actor
    public void c1(II_Result r) {
        Integer v = q.dequeue();
        if (v != null) r.r1 = 1;
    }

    @Actor
    public void c2(II_Result r) {
        Integer v = q.dequeue();
        if (v != null) r.r2 = 1;
    }
}

