package org.example.jcstress;

import org.example.LockFreeQueue;
import org.openjdk.jcstress.annotations.*;
import org.openjdk.jcstress.infra.results.II_Result;

@JCStressTest
@Outcome(id = "0, 0", expect = Expect.FORBIDDEN, desc = "deadlock. никто не работает")
@Outcome(expect = Expect.ACCEPTABLE, desc = "хотя бы кто-то прогрессирует")
@State
public class LockFreeProgressTest {

    LockFreeQueue<Integer> q = new LockFreeQueue<>();

    @Actor
    void enqueueMany(II_Result r) {
        int cnt = 0;
        for (int i = 0; i < 1000; i++) {
            q.enqueue(i);
            cnt++;
        }
        r.r1 = cnt;
    }

    @Actor
    void dequeueMany(II_Result r) {
        int cnt = 0;
        while (cnt < 1000) {  // while вместо for
            Integer x = q.dequeue();
            if (x != null) cnt++;
        }
        r.r2 = cnt;
    }
}
