package org.example.multithreading;
public class ThreadMethod {
  static class MyThread extends Thread {
    public MyThread(String name) { //we can assign name to the current thread.
      super(name);
    }
    @Override
    public void run() {
      for (int i = 0; i < 5; i++) {
        System.out.println(Thread.currentThread().getName() + " running...");
        try {
          Thread.sleep(1000);
          // yield:  A hint to the scheduler that the current thread is willing to yield its current use of a processor. 
          // The scheduler is free to ignore this hint.
          Thread.yield();
        } catch (InterruptedException e) {
          throw new RuntimeException(e);
        }
      }
    }
  }
  public static void main(String[] args) throws InterruptedException {
    MyThread t1 = new MyThread("t1");
    MyThread t2 = new MyThread("t2");
    // Marks this thread as either a daemon thread or a user thread. 
    // The Java Virtual Machine exits when the only threads running are all daemon threads.
    // This method must be invoked before the thread is started.
    t1.setDaemon(true);

    t1.start(); // this will make the run execute.
    t2.start();
    
    t1.interrupt(); // interrupt the thread wherever it is.
    t1.join(); // main thread (which is the current thread) will wait for the myThread to terminate.
    System.out.println("Main thread terminated.");
    t1.setPriority(Thread.NORM_PRIORITY);
     //
  }
}
