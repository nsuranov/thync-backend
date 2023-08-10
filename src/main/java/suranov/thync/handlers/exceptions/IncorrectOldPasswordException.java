package suranov.thync.handlers.exceptions;

public class IncorrectOldPasswordException extends RuntimeException {
    public IncorrectOldPasswordException(String message) {
        super(message);
    }
    public IncorrectOldPasswordException(String message, Throwable cause) {
        super(message, cause);
    }
}
