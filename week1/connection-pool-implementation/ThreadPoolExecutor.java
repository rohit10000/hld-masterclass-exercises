public interface ThreadPoolExecutor<T> {
    void execute(T task);
    void shutdown();
}
