package com.rockup.app.exception;

import java.util.UUID;

public class MessageNotFoundException extends WebSocketException {
    public MessageNotFoundException(UUID uuid) {
        super(String.format("Message with id %s not found", uuid.toString()));
    }

    @Override
    public String getCode() {
        return "message_not_found";
    }
}
