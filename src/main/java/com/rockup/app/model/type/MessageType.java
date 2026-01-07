package com.rockup.app.model.type;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum MessageType {
    CREATE_MESSAGE, EDIT_MESSAGE, GET_MESSAGES, UNKNOWN;

    @JsonCreator
    public static MessageType from(String value) {
        try {
            return MessageType.valueOf(value);
        } catch (Exception e) {
            return UNKNOWN;
        }
    }
}
