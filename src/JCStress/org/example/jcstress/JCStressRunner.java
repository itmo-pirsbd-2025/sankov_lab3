package org.example.jcstress;

public class JCStressRunner {
    public static void main(String[] args) throws Exception {
        org.openjdk.jcstress.Main.main(new String[] {
                "-t", "EnqueueDequeue",
                "-ti", "200",
                "-it", "1",
                "-f", "1",
                "-m", "quick",
                "-v",
                "-r", "target/jcstress-quick-1"
        });
        org.openjdk.jcstress.Main.main(new String[] {
                "-t", "FifoOrder",
                "-ti", "200",
                "-it", "1",
                "-f", "1",
                "-m", "quick",
                "-v",
                "-r", "target/jcstress-quick-2"
        });
        org.openjdk.jcstress.Main.main(new String[] {
                "-t", "NoDuplication",
                "-ti", "200",
                "-it", "1",
                "-f", "1",
                "-m", "quick",
                "-v",
                "-r", "target/jcstress-quick-3"
        });
        org.openjdk.jcstress.Main.main(new String[] {
                "-t", "LockFreeProgressTest",
                "-ti", "200",
                "-it", "1",
                "-f", "1",
                "-m", "quick",
                "-v",
                "-r", "target/jcstress-quick-4"
        });
    }
}