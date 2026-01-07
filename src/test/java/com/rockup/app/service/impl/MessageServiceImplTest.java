package com.rockup.app.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import com.rockup.app.dao.MessageRepository;
import com.rockup.app.exception.NotFoundException;
import com.rockup.app.exception.VersionMismatchException;
import com.rockup.app.mapper.MessageMapper;
import com.rockup.app.model.entity.MessageEntity;
import com.rockup.app.model.request.MessageRequest;
import com.rockup.app.model.response.MessageResponse;
import com.rockup.app.model.type.MessageStatus;
import com.rockup.app.model.type.MessageType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class MessageServiceImplTest {
    @Mock
    private MessageRepository repository;
    @Mock
    private MessageMapper mapper;

    @InjectMocks
    private MessageServiceImpl service;


    @Test
    void createMessage() {
        var chatId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        MessageRequest request = MessageRequest.builder()
                .type(MessageType.CREATE_MESSAGE)
                .chatId(chatId)
                .userId(userId)
                .payload("Test message")
                .build();
        int lastN = 5;
        var entity = new MessageEntity(UUID.randomUUID(), request.getUserId(), request.getChatId(),
                lastN + 1, request.getPayload());
        var expected = new MessageResponse(MessageStatus.MESSAGE_CREATED, entity.getPayload(), entity.getId(),
                entity.getMessageChatN(), entity.getVersion());
        Mockito.doReturn(lastN).when(repository).findLastMessageNumberForUpdate(chatId);
        Mockito.doReturn(entity).when(mapper).requestToEntity(request, lastN + 1);

        var actual = service.createMessage(request);

        assertEquals(expected, actual);
        Mockito.verify(repository, Mockito.times(1)).findLastMessageNumberForUpdate(chatId);
        Mockito.verify(mapper, Mockito.times(1)).requestToEntity(request, lastN + 1);
        Mockito.verify(repository, Mockito.times(1)).save(entity);
    }

    @Test
    void editMessage() {
        var messageId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var chatId = UUID.randomUUID();
        MessageRequest request = MessageRequest.builder()
                .type(MessageType.EDIT_MESSAGE)
                .messageId(messageId)
                .version(0)
                .payload("updated test message")
                .build();
        var entity = new MessageEntity(messageId, userId, chatId, 5, "Test message");
        entity.setVersion(0);
        var updatedEntity = new MessageEntity(messageId, userId, chatId, 5, request.getPayload());
        updatedEntity.setVersion(1);
        var expected = new MessageResponse(null, request.getPayload(), messageId, entity.getMessageChatN(), updatedEntity.getVersion());
        Mockito.doReturn(Optional.of(entity)).when(repository).findById(messageId);
        Mockito.doReturn(updatedEntity).when(repository).save(Mockito.any());
        Mockito.doReturn(expected).when(mapper).entityToResponse(updatedEntity);

        var actual = service.editMessage(request);

        assertEquals(expected, actual);
        Mockito.verify(repository, Mockito.times(1)).findById(messageId);
        Mockito.verify(repository, Mockito.times(1)).save(Mockito.any());
        Mockito.verify(mapper, Mockito.times(1)).entityToResponse(updatedEntity);
    }

    @Test
    void getMessages() {
        var chatId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var request = MessageRequest.builder()
                .type(MessageType.GET_MESSAGES)
                .chatId(chatId)
                .userId(userId)
                .limit(10)
                .offset(0)
                .build();
        var messageEntity = new MessageEntity(UUID.randomUUID(), userId, chatId, 5, request.getPayload());
        Page<MessageEntity> page = new PageImpl<>(List.of(messageEntity));
        MessageResponse resp = new MessageResponse(null, request.getPayload(), messageEntity.getId(), messageEntity.getMessageChatN(), messageEntity.getVersion());
        var pageRequest = PageRequest.of(request.getOffset() / request.getLimit(), request.getLimit());
        Mockito.doReturn(page).when(repository)
                .findByChatIdAndUserIdOrderByMessageChatNAsc(chatId, userId, pageRequest);
        Mockito.doReturn(List.of(resp)).when(mapper).entityListToResponseList(page.getContent());

        var actual = service.getMessages(request);

        assertNotNull(actual.getData());
        assertEquals(actual.getData().getFirst(), resp);
        Mockito.verify(repository, Mockito.times(1))
                .findByChatIdAndUserIdOrderByMessageChatNAsc(chatId, userId, pageRequest);
        Mockito.verify(mapper, Mockito.times(1)).entityListToResponseList(page.getContent());
    }

    @Test
    void editMessage_ShouldThrowVersionMismatchException() {
        var chatId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var messageId = UUID.randomUUID();
        var request = MessageRequest.builder().type(MessageType.EDIT_MESSAGE).messageId(messageId).version(1).build();
        var messageEntity = new MessageEntity(messageId, userId, chatId, 5, request.getPayload());
        messageEntity.setVersion(2);
        var expectedMessage = "Version mismatch. Expected=" + messageEntity.getVersion() + ", actual=" + request.getVersion();
        Mockito.doReturn(Optional.of(messageEntity)).when(repository).findById(messageId);

        var exception = assertThrows(VersionMismatchException.class, () -> service.editMessage(request));
        assertEquals(exception.getCode(), "version_mismatch");
        assertEquals(exception.getMessage(), expectedMessage);
    }

    @Test
    void editMessage_ShouldThrowNotFoundException() {
        var messageId = UUID.randomUUID();
        var request = MessageRequest.builder().type(MessageType.EDIT_MESSAGE).messageId(messageId).version(1).build();

        Mockito.doReturn(Optional.empty()).when(repository).findById(request.getMessageId());

        var exception = assertThrows(NotFoundException.class, () -> service.editMessage(request));
        assertEquals(exception.getMessage(), "Message not found");
        assertEquals(exception.getCode(), "message_not_found");
    }
}