package org.example;

public interface ConcurrentQueue<T> {
    void enqueue(T value);
    T dequeue();
}
