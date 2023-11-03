package com.app.comentarioserver.exception;

public class UnableToAddFeedbackException extends RuntimeException {

    public UnableToAddFeedbackException(String message) {
        super(message);
    }

    public UnableToAddFeedbackException(String message, Throwable cause) {
        super(message, cause);
    }
}
