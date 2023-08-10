package suranov.thync.handlers.exceptions;

public class TeamAlreadyExistException extends RuntimeException {
    public TeamAlreadyExistException(String message) {
        super(message);
    }

    public TeamAlreadyExistException(String message, Throwable cause) {
        super(message, cause);
    }
}
