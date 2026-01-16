package org.example;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue<T> implements ConcurrentQueue<T> {

    private static final class Node<T> {
        final T value;
        final AtomicReference<Node<T>> next = new AtomicReference<>(null);

        Node(T value) {
            this.value = value;
        }
    }

    private final AtomicReference<Node<T>> head;
    private final AtomicReference<Node<T>> tail;

    public LockFreeQueue() {
        Node<T> dummy = new Node<>(null);
        head = new AtomicReference<>(dummy);
        tail = new AtomicReference<>(dummy);
    }

    @Override
    public void enqueue(T value) {
        if (value == null) throw new NullPointerException();

        Node<T> node = new Node<>(value);

        while (true) {
            Node<T> last = tail.get();
            Node<T> next = last.next.get();

            if (last == tail.get()) {
                if (next == null) {
                    if (last.next.compareAndSet(null, node)) {
                        tail.compareAndSet(last, node);
                        return;
                    }
                } else {
                    tail.compareAndSet(last, next);
                }
            }
        }
    }

    @Override
    public T dequeue() {
        while (true) {
            Node<T> first = head.get();
            Node<T> last = tail.get();
            Node<T> next = first.next.get();

            if (first == head.get()) {
                if (first == last) {
                    if (next == null) {
                        return null;
                    }
                    tail.compareAndSet(last, next);
                } else {
                    if (head.compareAndSet(first, next)) {
                        return next.value;
                    }
                }
            }
        }
    }
}
