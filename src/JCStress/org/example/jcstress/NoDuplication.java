package org.example.jcstress;

import org.example.LockFreeQueue;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

@JCStressTest
@Description("один элемент - одному consumer'у")
@Outcome(id = "1, 0", expect = Expect.ACCEPTABLE, desc = "первый взял")
@Outcome(id = "0, 1", expect = Expect.ACCEPTABLE, desc = "второй взял")
@Outcome(id = "0, 0", expect = Expect.ACCEPTABLE, desc = "никто не взял")
@Outcome(id = "1, 1", expect = Expect.FORBIDDEN, desc = "дубликат! баг!!!")
@State
public class NoDuplication {

    LockFreeQueue<Integer> q = new LockFreeQueue<>();

    @Actor
    public void put() {
        q.enqueue(1);
    }

    @Actor
    public void take1(II_Result res) {
        if (q.dequeue() != null) {
            res.r1 = 1;
        }
    }

    @Actor
    public void take2(II_Result res) {
        if (q.dequeue() != null) res.r2 = 1;
    }
}
