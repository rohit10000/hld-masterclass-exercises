package org.example;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class BankAccount {
    private int balance;
    BankAccount(int balance) {
        this.balance = balance;
    }
    Lock lock = new ReentrantLock(true); // fair means every thread will get equal cpu opportunity.
    // its prevents starvation.
    void withdraw(int amount) {
        System.out.println(Thread.currentThread().getName() + " attempting to withdraw " + amount);
//        lock.lock(); // it will like as synchronized it will wait until. Current thread waiting would be in blocked waiting state.
        try {
//            lock.lockInterruptibly();
            if (lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                try {
                    if (balance >= amount) {
                        System.out.println(Thread.currentThread().getName() + " proceeding with withdrawal.");
                        Thread.sleep(5000);
                        balance -= amount;
                        System.out.println(Thread.currentThread().getName() + " completed withdrawal. Remaining balance: " + balance);
                    } else {
                        System.out.println(Thread.currentThread().getName() + " insufficient balance");
                    }
                } catch (Exception e) {
                    Thread.currentThread().interrupt(); // This is the good practice, to interrupt the thread in catch.
                } finally {
                    lock.unlock();
                }
            } else {
                System.out.println(Thread.currentThread().getName() + " could not acquire lock, will try later.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Good practice.
        }
        if (Thread.currentThread().isInterrupted()) {
            // Since we have interrupted the thread in case of exception we can make use of its interrupted state
            // and can perform post interrupt things here.
        }
    }
}

public class ExplicitLock {
    public static void main(String[] args) {
        BankAccount bankAccount = new BankAccount(100);
        Runnable task1 = () -> bankAccount.withdraw(50);
        Runnable task2 = () -> bankAccount.withdraw(50);
        Thread t1 = new Thread(task1);
        Thread t2 = new Thread(task2);

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }

}
