package com.rockup.app.validation.impl;

import com.rockup.app.exception.ValidationException;
import com.rockup.app.model.request.MessageRequest;
import com.rockup.app.validation.MessageRequestValidator;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

@Component
public class DefaultMessageRequestValidator implements MessageRequestValidator {

    @Override
    public void validate(MessageRequest request) {
        if (ObjectUtils.isEmpty(request.getType())) {
            throw new ValidationException("type is required");
        }

        switch (request.getType()) {

            case CREATE_MESSAGE -> {
                require(request.getChatId(), "chatId");
                require(request.getUserId(), "userId");
                require(request.getPayload(), "payload");
            }

            case EDIT_MESSAGE -> {
                require(request.getMessageId(), "messageId");
                require(request.getVersion(), "version");
                require(request.getPayload(), "payload");
            }

            case GET_MESSAGES -> {
                require(request.getChatId(), "chatId");
                require(request.getUserId(), "userId");
                requirePositive(request.getLimit(), "limit");
                requireNonNegative(request.getOffset(), "offset");
            }

            case UNKNOWN -> {
                throw new ValidationException("Unknown message type");
            }
        }
    }

    private void require(Object value, String field) {
        if (value == null) {
            throw new ValidationException(field + " is required");
        }
    }

    private void requirePositive(Integer value, String field) {
        if (value == null || value <= 0) {
            throw new ValidationException(field + " must be > 0");
        }
    }

    private void requireNonNegative(Integer value, String field) {
        if (value == null || value < 0) {
            throw new ValidationException(field + " must be >= 0");
        }
    }
}
