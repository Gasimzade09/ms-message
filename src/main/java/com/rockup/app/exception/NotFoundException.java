package com.rockup.app.exception;

public class NotFoundException extends WebSocketException {
    public NotFoundException(String message) {
        super(message);
    }

    @Override
    public String getCode() {
        return "message_not_found";
    }
}
