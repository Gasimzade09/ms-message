package com.rockup.app.exception;

public class ValidationException extends WebSocketException {
    public ValidationException(String message) {
        super(message);
    }

    @Override
    public String getCode() {
        return "empty_payload";
    }
}
