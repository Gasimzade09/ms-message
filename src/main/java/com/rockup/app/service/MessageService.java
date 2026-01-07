package com.rockup.app.service;

import com.rockup.app.model.request.MessageRequest;
import com.rockup.app.model.response.MessageResponse;
import com.rockup.app.model.response.PageableResponse;
import java.util.List;

public interface MessageService {

    MessageResponse createMessage(MessageRequest request);

    MessageResponse editMessage(MessageRequest request);

    PageableResponse<MessageResponse> getMessages(MessageRequest messageRequest);
}
