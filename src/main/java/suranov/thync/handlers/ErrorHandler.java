package suranov.thync.handlers;

import suranov.thync.handlers.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class ErrorHandler {

    public static class ErrorResponse {
        private final String message;
        private final String errorType;

        public ErrorResponse(String message, String errorType) {
            this.message = message;
            this.errorType = errorType;
        }

        public String getMessage() {
            return message;
        }

        public String getErrorType() {
            return errorType;
        }
    }

    @ExceptionHandler(ArticleNotFoundException.class)
    @ResponseBody
    public Mono<ResponseEntity<ErrorResponse>> handleSameUserReactionException(ServerWebExchange exchange, ArticleNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName());
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }
    @ExceptionHandler(SameUserReactionException.class)
    @ResponseBody
    public Mono<ResponseEntity<ErrorResponse>> handleSameUserReactionException(ServerWebExchange exchange, SameUserReactionException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName());
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(CustomException.class)
    @ResponseBody
    public Mono<ResponseEntity<ErrorResponse>> handleSameUserReactionException(ServerWebExchange exchange, CustomException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName());
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(TeamNotFoundException.class)
    @ResponseBody
    public Mono<ResponseEntity<ErrorResponse>> handleTeamNotFoundException(ServerWebExchange exchange, TeamNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName());
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }


    @ExceptionHandler(TeamAlreadyExistException.class)
    @ResponseBody
    public Mono<ResponseEntity<ErrorResponse>> handleTeamAlreadyExistException(ServerWebExchange exchange, TeamAlreadyExistException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName());
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }
    @ExceptionHandler(CommentNotFoundException.class)
    @ResponseBody
    public Mono<ResponseEntity<ErrorResponse>> handleCommentNotFoundException(ServerWebExchange exchange, CommentNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName());
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(SameReactionException.class)
    @ResponseBody
    public Mono<ResponseEntity<ErrorResponse>> handleCustomException(ServerWebExchange exchange, SameReactionException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName());
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(IdeaNotFoundException.class)
    @ResponseBody
    public Mono<ResponseEntity<ErrorResponse>> handleIdeaNotFoundException(ServerWebExchange exchange, IdeaNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName());
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }


    @ExceptionHandler(UserAlreadyExistsException.class)
    @ResponseBody
    public Mono<ResponseEntity<ErrorResponse>> handleUserAlreadyExistsException(ServerWebExchange exchange, UserAlreadyExistsException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName());
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(UsernameTokenMismatchException.class)
    @ResponseBody
    public Mono<ResponseEntity<ErrorResponse>> handleUsernameTokenMismatchException(ServerWebExchange exchange, UsernameTokenMismatchException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName());
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(IncorrectOldPasswordException.class)
    @ResponseBody
    public Mono<ResponseEntity<ErrorResponse>> handleIncorrectOldPasswordException(ServerWebExchange exchange, IncorrectOldPasswordException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName());
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseBody
    public Mono<ResponseEntity<ErrorResponse>> handleUserNotFoundException(ServerWebExchange exchange, UserNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), ex.getClass().getSimpleName());
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND));
    }
}


