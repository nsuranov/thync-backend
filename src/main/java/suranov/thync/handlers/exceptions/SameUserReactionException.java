package suranov.thync.handlers.exceptions;

public class SameUserReactionException extends RuntimeException {

    public SameUserReactionException(String message) {
        super(message);
    }

    public SameUserReactionException(String message, Throwable cause) {
        super(message, cause);
    }
}
