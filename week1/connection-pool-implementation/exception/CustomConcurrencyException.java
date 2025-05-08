package exception;

public class CustomConcurrencyException extends RuntimeException {
    public CustomConcurrencyException(String message) {
        super(message);
    }
}
