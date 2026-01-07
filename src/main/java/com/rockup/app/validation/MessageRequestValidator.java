package com.rockup.app.validation;

import com.rockup.app.model.request.MessageRequest;

public interface MessageRequestValidator {
    void validate(MessageRequest request);
}
