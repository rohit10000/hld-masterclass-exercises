import java.util.concurrent.Semaphore;

class Resource {
    private String name;
    private Semaphore semaphore;
    public Resource(String name) {
        this.name = name;
        this.semaphore = new Semaphore(2);
    }

    public String getName() {
        return this.name;
    }

    public void allocate() {
        System.out.printf("%s - Trying allocating resoure %s ...\n", Thread.currentThread().getName(), this.name);
        try {
            this.semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread().getName() + " interrupted");
        }
        System.out.printf("%s - Successfully allocated resoure %s.\n", Thread.currentThread().getName(), this.name);
    }

    public void release() {
        System.out.printf("%s - Trying releasing resoure %s ...\n", Thread.currentThread().getName(), this.name);
        this.semaphore.release();
        System.out.printf("%s - Successfully released resoure %s.\n", Thread.currentThread().getName(), this.name);
    }

}

public class DeadlockExample {
    public static void main(String[] args) {
        Resource resourceA = new Resource("A");
        Resource resourceB = new Resource("B");

        Resource[][] resources = new Resource[10][2];
        for (int i = 0; i < 10; i++) {
            // Simulating non order resource allocation for deadlock.
            //It can be solved by imposing ordering of resource allocation.
            resources[i][i % 2] = resourceA;
            resources[i][(i + 1) % 2] = resourceB;
        }

        Thread[] threads = new Thread[10];
        for (int i = 0; i < threads.length; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                resources[finalI][0].allocate();

                resources[finalI][1].allocate();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                resources[finalI][0].release();
                resources[finalI][1].release();
                System.out.println("Done processing " + Thread.currentThread().getName());
            }, "Worker-thread-" + i);
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].start();
        }
        try {
            for (int i = 0; i < threads.length; i++) {
                threads[i].join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.out.println("All threads done processing.");

    }
}