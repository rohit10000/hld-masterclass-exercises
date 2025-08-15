package org.example;


import java.util.PriorityQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// in case of reentrant lock, same thread can hold the lock multiple times.
// And would have to unlock also that many times to unlock.

public class ReenterantExample {
    private final Lock lock = new ReentrantLock();

    public void outerMethod() {
        lock.lock();
//        lock.lockInterruptibly();
        try {
            System.out.println("Outer method");
            innerMethod();
        } finally {
            lock.unlock();
        }
    }
    public void innerMethod() {
        lock.lock();
        try {
            System.out.println("Inner method");
        } finally {
            lock.unlock();
        }
    }
    public static void main(String[] args) {
        ReenterantExample reenterantExample = new ReenterantExample();
        reenterantExample.outerMethod();
        Integer i;
        Integer j;
        PriorityQueue<Integer> pq = new PriorityQueue<>();
        System.out.println(pq.peek());
    }
}
