package suranov.thync.handlers.exceptions;

public class UsernameTokenMismatchException extends RuntimeException {
    public UsernameTokenMismatchException(String message) {
        super(message);
    }
    public UsernameTokenMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
