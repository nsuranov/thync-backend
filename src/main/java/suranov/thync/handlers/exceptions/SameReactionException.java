package suranov.thync.handlers.exceptions;

public class SameReactionException extends RuntimeException {

    public SameReactionException(String message) {
        super(message);
    }

    public SameReactionException(String message, Throwable cause) {
        super(message, cause);
    }

}

