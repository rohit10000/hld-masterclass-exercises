import blockingQueue.BlockingQueue;
import blockingQueue.CustomBlockingQueue;

public class CustomThreadPoolExecutor implements ThreadPoolExecutor<Runnable> {
    private final int coreThread;
    private final BlockingQueue<Runnable> blockingQueue;
    private final Thread[] workers;
    private volatile boolean isShutdown = false;

    public CustomThreadPoolExecutor(int coreThreadCount) {
        this.coreThread = coreThreadCount;
        this.blockingQueue = new CustomBlockingQueue(coreThreadCount);
        this.workers = new Thread[coreThreadCount];
        for (int i = 0; i < coreThreadCount; i++) {
            this.workers[i] = new Thread(new Task());
            this.workers[i].start();
        }
    }

    @Override
    public void execute(Runnable task) {
        if (this.isShutdown) {
            throw new IllegalStateException("Executor service is shutdown.");
        }
        blockingQueue.put(task);
    }

    @Override
    public void shutdown() {
        if (!this.isShutdown) {
            this.isShutdown = true;
            for (int i = 0; i < coreThread; i++) {
                this.workers[i].interrupt();
            }
        }
    }

    private class Task implements Runnable {
        @Override
        public void run() {
            while (!isShutdown) {
                try {
                    Runnable task = blockingQueue.take();
                    task.run();
                } catch (Exception ex) {
                    if (isShutdown) {
                        break;
                    }
                }
            }
        }
    }
}
