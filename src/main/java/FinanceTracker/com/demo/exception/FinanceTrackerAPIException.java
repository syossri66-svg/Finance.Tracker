package FinanceTracker.com.demo.exception;


import org.springframework.http.HttpStatus;

public class FinanceTrackerAPIException extends RuntimeException {

    private final HttpStatus status;
    private final String message;

    public FinanceTrackerAPIException(HttpStatus status, String message) {
        super(message);
        this.status = status;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}