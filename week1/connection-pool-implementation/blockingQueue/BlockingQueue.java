package blockingQueue;

public interface BlockingQueue<T> {
    T take();
    void put(T value);

    int size();
}
