package org.example;

class Counter {
    private int count;
    public Counter() {
        this.count = 0;
    }
    synchronized public void increment() { // intrinsic (automatic) lock
        count++;
    }
    public int getCount() {
        return count;
    }
}

class MyThread extends Thread {
    private final Counter counter;
    public MyThread(Counter counter) {
        this.counter = counter;
    }
    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            counter.increment();
        }
    }
}

public class Synchronization {
    public static void main(String[] args) throws InterruptedException {
        Counter counter = new Counter();
        MyThread t1 = new MyThread(counter);
        MyThread t2 = new MyThread(counter);

        t1.start();
        t2.start();

        // Main thread will wait for the completion of both threads t1 and t2.
        t1.join();
        t2.join();

        System.out.println("counter value: " + counter.getCount());
    }
}
