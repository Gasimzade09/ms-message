package com.rockup.app.websocket;

import com.rockup.app.exception.ErrorResponse;
import com.rockup.app.exception.WebSocketException;
import com.rockup.app.model.request.MessageRequest;
import com.rockup.app.service.MessageService;
import com.rockup.app.validation.MessageRequestValidator;
import java.io.IOException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@AllArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {
    private final ObjectMapper objectMapper;
    private final MessageService messageService;
    private final MessageRequestValidator validator;

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        log.info("ActionLog.handleTextMessage.Start");
        String payload = message.getPayload();
        try {
            var messageRequest = objectMapper.readValue(payload, MessageRequest.class);
            validator.validate(messageRequest);
            switch (messageRequest.getType()) {
                case EDIT_MESSAGE -> sendMessage(session, messageService.editMessage(messageRequest));
                case CREATE_MESSAGE -> sendMessage(session, messageService.createMessage(messageRequest));
                case GET_MESSAGES -> sendMessage(session, messageService.getMessages(messageRequest));
                case UNKNOWN -> sendMessage(session, new ErrorResponse("unknown_type","Unknown message type"));
            }
        } catch (WebSocketException e) {
            log.warn("Business error: {}", e.getMessage());
            sendMessage(session, new ErrorResponse(e.getCode(), e.getMessage()));
        } catch (Exception e) {
            log.error("Unexpected error", e);
            sendMessage(session, new ErrorResponse("INTERNAL_ERROR", "Unexpected server error"));
        }
        log.info("ActionLog.handleTextMessage.End");
    }

    private void sendMessage(WebSocketSession session, Object response) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(response)));
        } catch (IOException e) {
            log.error("Failed to send message", e);
        }
    }
}

