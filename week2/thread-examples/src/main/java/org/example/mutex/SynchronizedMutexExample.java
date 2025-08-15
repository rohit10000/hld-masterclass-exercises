package org.example.mutex;

class SharedResource {
    private int counter = 0;

    // Method-level synchronization (uses 'this' as mutex)
    public synchronized void incrementSync() {
        counter++;
        System.out.println(Thread.currentThread().getName() +
                " incremented counter to: " + counter);

        // Simulate some work
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // Block-level synchronization
    public void incrementBlock() {
        synchronized(this) {
            counter++;
            System.out.println(Thread.currentThread().getName() +
                    " (block sync) incremented counter to: " + counter);

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    public synchronized int getCounter() {
        return counter;
    }
}

public class SynchronizedMutexExample {
    public static void main(String[] args) {
        SharedResource resource = new SharedResource();

        // Create multiple threads that will compete for access
        Thread[] threads = new Thread[5];

        for (int i = 0; i < threads.length; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> {
                // Each thread tries to increment the counter multiple times
                for (int j = 0; j < 3; j++) {
                    if (threadNum % 2 == 0) {
                        resource.incrementSync();
                    } else {
                        resource.incrementBlock();
                    }
                }
            }, "Thread-" + i);
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("Final counter value: " + resource.getCounter());
    }
}