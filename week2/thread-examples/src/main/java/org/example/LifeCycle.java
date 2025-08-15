package org.example.multithreading;

class MyThread implements Runnable {

  @Override
  public void run() {
      System.out.println("Running...");
      System.out.println("debug 4: " + Thread.currentThread().getState());
    try {
      Thread.sleep(5000);
      System.out.println("debug 6: " + Thread.currentThread().getState()); // timed-waiting state
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}

public class LifeCycle {
  public static void main(String[] args) throws InterruptedException {
    System.out.println("debug 1: " + Thread.currentThread().getName());
    
    MyThread runnable = new MyThread();
    Thread t1 = new Thread(runnable); //new - creating a new thread
    System.out.println("debug 2: " + t1.getState());
    t1.start(); // runnable state - either it would be running or it would be waiting to run.
    System.out.println("debug 3: " + t1.getState());
    Thread.sleep(1000);
    System.out.println("debug 5: " + t1.getState()); // timed-waiting
    t1.join(); // main thread will wait for the t1 to complete.
    System.out.println("debug 7: " + t1.getState()); //terminated
  }
}
