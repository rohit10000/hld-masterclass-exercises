package exception;

public class SemaphoreOperationException extends RuntimeException {
    public SemaphoreOperationException(String message) {
        super(message);
    }
}
