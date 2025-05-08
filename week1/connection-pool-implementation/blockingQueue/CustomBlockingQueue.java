package blockingQueue;

import exception.SemaphoreOperationException;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class CustomBlockingQueue implements BlockingQueue<Runnable> {
    private final Semaphore enqueSemaphore;
    private final Semaphore dequeSemaphore;

    public CustomBlockingQueue(int n) {
        enqueSemaphore = new Semaphore(n);
        dequeSemaphore = new Semaphore(0);
    }
    public CustomBlockingQueue() {
        this(3);
    }

    Queue<Runnable> queue = new LinkedList<>();

    @Override
    public Runnable take() {
        try {
            dequeSemaphore.acquire();
            Runnable runnable;
            synchronized (this) {
                runnable = queue.poll();
            }
            enqueSemaphore.release();
            return runnable;
        } catch (InterruptedException e) {
            throw new SemaphoreOperationException("Issue in semaphore lock operation");
        }
    }

    @Override
    public void put(Runnable value) {
        try {
            enqueSemaphore.acquire();
            synchronized (this) {
                queue.offer(value);
            }
            dequeSemaphore.release();
        } catch (InterruptedException e) {
            throw new SemaphoreOperationException("Issue in semaphore lock operation");
        }
    }

    @Override
    public int size() {
        synchronized (this) {
            return queue.size();
        }
    }


}
