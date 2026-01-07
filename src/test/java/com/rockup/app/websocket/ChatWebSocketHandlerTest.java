package com.rockup.app.websocket;

import static org.junit.jupiter.api.Assertions.*;
import com.rockup.app.model.request.MessageRequest;
import com.rockup.app.model.type.MessageType;
import java.net.URI;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class ChatWebSocketHandlerTest {

    @LocalServerPort
    int port;

    private WebSocketSession session;
    private CompletableFuture<String> responseFuture;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() throws Exception {
        responseFuture = new CompletableFuture<>();

        TextWebSocketHandler clientHandler = new TextWebSocketHandler() {
            @Override
            protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                responseFuture.complete(message.getPayload());
            }

            @Override
            public void handleTransportError(WebSocketSession session, Throwable exception) {
                responseFuture.completeExceptionally(exception);
            }
        };

        StandardWebSocketClient client = new StandardWebSocketClient();

        session = client.execute(
                clientHandler,
                null,
                URI.create("ws://localhost:" + port + "/ws/chat")
        ).get(3, TimeUnit.SECONDS);
    }

    @AfterEach
    void tearDown() throws Exception {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @Test
    void createMessage_shouldReturnMessageCreated() throws Exception {
        MessageRequest request = new MessageRequest(
                MessageType.CREATE_MESSAGE,
                UUID.fromString("11111111-1111-1111-1111-111111111111"),
                UUID.fromString("22222222-2222-2222-2222-222222222222"),
                null,
                null,
                "hello websocket",
                null,
                null
        );

        String jsonRequest = objectMapper.writeValueAsString(request);

        session.sendMessage(new TextMessage(jsonRequest));

        String response = responseFuture.get(3, TimeUnit.SECONDS);

        assertNotNull(response);
        assertTrue(response.contains("MESSAGE_CREATED"));
        assertTrue(response.contains("hello websocket"));
    }

    @Test
    void invalidMessageType_shouldReturnError() throws Exception {
        String invalidJson = """
                    {
                      "type": "INVALID_TYPE",
                      "chatId": "11111111-1111-1111-1111-111111111111",
                      "userId": "22222222-2222-2222-2222-222222222222",
                      "payload": "oops"
                    }
                """;

        session.sendMessage(new TextMessage(invalidJson));

        String response = responseFuture.get(3, TimeUnit.SECONDS);

        assertNotNull(response);
        assertTrue(response.contains("validation_error") || response.contains("Unknown message type"));
    }

}