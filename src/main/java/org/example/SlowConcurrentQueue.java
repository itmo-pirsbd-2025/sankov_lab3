package org.example;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

public class SlowConcurrentQueue<T> implements ConcurrentQueue<T> {

    private static class Node<T> {
        volatile T value;
        volatile Node<T> next;

        Node(T value) {
            this.value = value;
        }
    }

    private volatile Node<T> head;
    private volatile Node<T> tail;

    private final ReentrantLock lock = new ReentrantLock();
    private final AtomicInteger size = new AtomicInteger();

    @Override
    public void enqueue(T value) {
        Node<T> node = new Node<>(value);

        lock.lock();
        try {
            if (tail == null) {
                head = tail = node;
            } else {
                tail.next = node;
                tail = node;
            }
            size.incrementAndGet();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public T dequeue() {
        lock.lock();
        try {
            if (head == null) {
                return null;
            }

            Node<T> n = head;
            head = n.next;
            if (head == null) {
                tail = null;
            }

            size.decrementAndGet();
            return n.value;
        } finally {
            lock.unlock();
        }
    }

    public int size() {
        return size.get();
    }
}

