package com.rockup.app.exception;

public abstract class WebSocketException extends RuntimeException {
    protected WebSocketException(String message) {
        super(message);
    }

    public abstract String getCode();
}
