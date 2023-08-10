package suranov.thync.handlers.exceptions;

public class IdeaNotFoundException extends RuntimeException{
    public IdeaNotFoundException(String message) {
        super(message);
    }
    public IdeaNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
