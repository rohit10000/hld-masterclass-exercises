package org.example;

class MultiConditionExample {
    private int value = 0;
    private boolean isEven = true;

    // Wait for even values
    public synchronized void waitForEven() throws InterruptedException {
        while (!isEven || value == 0) {
            System.out.println(Thread.currentThread().getName() + " waiting for even value");
            wait();
        }
        System.out.println(Thread.currentThread().getName() + " got even value: " + value);
    }

    // Wait for odd values
    public synchronized void waitForOdd() throws InterruptedException {
        while (isEven || value == 0) {
            System.out.println(Thread.currentThread().getName() + " waiting for odd value");
            wait();
        }
        System.out.println(Thread.currentThread().getName() + " got odd value: " + value);
    }

    // Set value and notify all waiting threads
    public synchronized void setValue(int newValue) {
        this.value = newValue;
        this.isEven = (newValue % 2 == 0);
        System.out.println("Set value to: " + newValue + " (isEven: " + isEven + ")");

        // Use notifyAll() because threads are waiting for different conditions
        notifyAll();
    }
}

public class NotifyAllExample {
    public static void main(String[] args) {
        MultiConditionExample example = new MultiConditionExample();

        // Create threads waiting for even values
        Thread evenWaiter1 = new Thread(() -> {
            try {
                example.waitForEven();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "EvenWaiter1");

        Thread evenWaiter2 = new Thread(() -> {
            try {
                example.waitForEven();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "EvenWaiter2");

        // Create threads waiting for odd values
        Thread oddWaiter1 = new Thread(() -> {
            try {
                example.waitForOdd();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "OddWaiter1");

        Thread oddWaiter2 = new Thread(() -> {
            try {
                example.waitForOdd();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }, "OddWaiter2");

        // Start all waiting threads
        evenWaiter1.start();
        evenWaiter2.start();
        oddWaiter1.start();
        oddWaiter2.start();

        // Give threads time to start waiting
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Set different values
        example.setValue(4);  // Even - should wake even waiters

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        example.setValue(7);  // Odd - should wake odd waiters
    }
}