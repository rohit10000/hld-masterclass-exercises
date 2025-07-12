package org.example;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BankAccountEntry {
    private int balance = 100;
    private final Lock lock = new ReentrantLock(); // Reentrant lock for thread-safe operations

    public void withdraw(int amount) {
        System.out.println(Thread.currentThread().getName() + " attempting to withdraw " + amount);
        try {
            // Try to acquire the lock within 1 second
            if (lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                if (balance >= amount) {
                    try {
                        System.out.println(Thread.currentThread().getName() + " proceeding with withdrawal");
                        Thread.sleep(3000); // Simulate time taken to process the withdrawal
                        balance -= amount;
                        System.out.println(Thread.currentThread().getName() + " completed withdrawal. Remaining balance: " + balance);
                    } catch (Exception e) {
                        Thread.currentThread().interrupt(); // Good practice to re-interrupt
                    } finally {
                        lock.unlock(); // Always release the lock
                    }
                } else {
                    System.out.println(Thread.currentThread().getName() + " insufficient balance");
                }
            } else {
                // Could not acquire the lock in time
                System.out.println(Thread.currentThread().getName() + " could not acquire the lock, will try later");
            }
        } catch (Exception e) {
            Thread.currentThread().interrupt(); // Good practice to re-interrupt
        }
    }
}

class LockMain {
    public static void main(String[] args) {
        BankAccountEntry sbi = new BankAccountEntry();
        Runnable task = () -> sbi.withdraw(50); // Both threads try to withdraw from the same account
        Thread t1 = new Thread(task, "Thread 1");
        Thread t2 = new Thread(task, "Thread 2");
        t1.start();
        t2.start();
    }
}