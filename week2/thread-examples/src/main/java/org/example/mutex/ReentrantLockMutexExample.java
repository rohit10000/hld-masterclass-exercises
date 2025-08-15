package org.example.mutex;

import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.TimeUnit;

class BankAccount {
    private final ReentrantLock mutex = new ReentrantLock();
    private double balance = 1000.0;

    public void withdraw(double amount) {
        mutex.lock(); // Acquire mutex
        try {
            System.out.println(Thread.currentThread().getName() +
                    " attempting to withdraw $" + amount);

            if (balance >= amount) {
                System.out.println(Thread.currentThread().getName() +
                        " checking balance: $" + balance);

                // Simulate processing time
                Thread.sleep(100);

                balance -= amount;
                System.out.println(Thread.currentThread().getName() +
                        " withdrew $" + amount +
                        ", new balance: $" + balance);
            } else {
                System.out.println(Thread.currentThread().getName() +
                        " insufficient funds for $" + amount +
                        ", balance: $" + balance);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.unlock(); // Always release mutex
        }
    }

    public boolean tryWithdraw(double amount, long timeoutMs) {
        try {
            // Try to acquire lock with timeout
            if (mutex.tryLock(timeoutMs, TimeUnit.MILLISECONDS)) {
                try {
                    System.out.println(Thread.currentThread().getName() +
                            " got lock, attempting withdrawal of $" + amount);

                    if (balance >= amount) {
                        Thread.sleep(50);
                        balance -= amount;
                        System.out.println(Thread.currentThread().getName() +
                                " successfully withdrew $" + amount +
                                ", new balance: $" + balance);
                        return true;
                    } else {
                        System.out.println(Thread.currentThread().getName() +
                                " insufficient funds");
                        return false;
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return false;
                } finally {
                    mutex.unlock();
                }
            } else {
                System.out.println(Thread.currentThread().getName() +
                        " could not acquire lock within " + timeoutMs +
                        "ms, giving up");
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    public void deposit(double amount) {
        mutex.lock();
        try {
            System.out.println(Thread.currentThread().getName() +
                    " depositing $" + amount);
            balance += amount;
            System.out.println(Thread.currentThread().getName() +
                    " deposited $" + amount +
                    ", new balance: $" + balance);
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            mutex.unlock();
        }
    }

    public double getBalance() {
        mutex.lock();
        try {
            return balance;
        } finally {
            mutex.unlock();
        }
    }

    // Demonstrate lock status information
    public void printLockInfo() {
        System.out.println("Lock held by current thread: " + mutex.isHeldByCurrentThread());
        System.out.println("Lock hold count: " + mutex.getHoldCount());
        System.out.println("Queued threads: " + mutex.getQueueLength());
        System.out.println("Is locked: " + mutex.isLocked());
    }
}

public class ReentrantLockMutexExample {
    public static void main(String[] args) {
        BankAccount account = new BankAccount();

        // Create multiple threads for different operations
        Thread[] threads = new Thread[6];

        // Withdrawal threads
        for (int i = 0; i < 3; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                if (threadId == 0) {
                    account.withdraw(300);
                } else {
                    // Use tryLock with timeout
                    account.tryWithdraw(200, 150);
                }
            }, "WithdrawThread-" + i);
        }

        // Deposit threads
        for (int i = 3; i < 6; i++) {
            threads[i] = new Thread(() -> {
                account.deposit(100);
            }, "DepositThread-" + (i - 3));
        }

        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }

        // Wait a bit and show lock info
        try {
            Thread.sleep(50);
            System.out.println("\n--- Lock Information ---");
            account.printLockInfo();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Wait for all threads to complete
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\nFinal balance: $" + account.getBalance());
    }
}