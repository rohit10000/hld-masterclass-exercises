package org.example;

import java.util.ArrayList;
import java.util.List;

class SharedBuffer {
    private List<Integer> buffer = new ArrayList<>();
    private final int MAX_SIZE = 5;

    // Producer adds items to buffer
    public synchronized void produce(int item) throws InterruptedException {
        // Wait if buffer is full
        while (buffer.size() == MAX_SIZE) {
            System.out.println("Buffer full, producer waiting...");
            wait(); // Release lock and wait
        }

        buffer.add(item);
        System.out.println("Produced: " + item + ", Buffer size: " + buffer.size());

        // Notify waiting consumers
        notify();
    }

    // Consumer removes items from buffer
    public synchronized int consume() throws InterruptedException {
        // Wait if buffer is empty
        while (buffer.isEmpty()) {
            System.out.println("Buffer empty, consumer waiting...");
            wait(); // Release lock and wait
        }

        int item = buffer.remove(0);
        System.out.println("Consumed: " + item + ", Buffer size: " + buffer.size());

        // Notify waiting producers
        notify();

        return item;
    }
}

class Producer1 implements Runnable {
    private SharedBuffer buffer;

    public Producer1(SharedBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= 10; i++) {
                buffer.produce(i);// Simulate work
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

class Consumer1 implements Runnable {
    private SharedBuffer buffer;

    public Consumer1(SharedBuffer buffer) {
        this.buffer = buffer;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 10; i++) {
                buffer.consume();
                Thread.sleep(150); // Simulate work
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

public class WaitNotifyExample {
    public static void main(String[] args) {
        SharedBuffer buffer = new SharedBuffer();

        Thread producer = new Thread(new Producer1(buffer), "Producer");
        Thread consumer = new Thread(new Consumer1(buffer), "Consumer");

        producer.start();
        consumer.start();

        try {
            producer.join();
            consumer.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Program completed!");
    }
}