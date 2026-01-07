package com.rockup.app.service.impl;

import com.rockup.app.dao.MessageRepository;
import com.rockup.app.exception.NotFoundException;
import com.rockup.app.exception.ValidationException;
import com.rockup.app.exception.VersionMismatchException;
import com.rockup.app.mapper.MessageMapper;
import com.rockup.app.model.entity.MessageEntity;
import com.rockup.app.model.request.MessageRequest;
import com.rockup.app.model.response.MessageResponse;
import com.rockup.app.model.response.PageableResponse;
import com.rockup.app.model.type.MessageStatus;
import com.rockup.app.service.MessageService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final MessageRepository repository;
    private final MessageMapper mapper;

    @Override
    @Transactional
    public MessageResponse createMessage(MessageRequest request) {
        log.info("ActionLog.createMessage.Start");
        int lastN = repository.findLastMessageNumberForUpdate(request.getChatId());
        var message = mapper.requestToEntity(request, lastN + 1);
        repository.save(message);
        log.info("ActionLog.createMessage.End");
        return new MessageResponse(MessageStatus.MESSAGE_CREATED, message.getPayload(), message.getId(), message.getMessageChatN(), message.getVersion());
    }

    @Override
    @Transactional
    public MessageResponse editMessage(MessageRequest request) {
        log.info("ActionLog.editMessage.Start");
        var message = repository.findById(request.getMessageId())
                .orElseThrow(() -> new NotFoundException("Message not found"));
        if (!message.getVersion().equals(request.getVersion())) {
            throw new VersionMismatchException(message.getVersion(), request.getVersion());
        }
        message.setPayload(request.getPayload());
        message = repository.saveAndFlush(message);
        log.info("ActionLog.editMessage.End");
        return mapper.entityToResponse(message);
    }

    @Override
    @Transactional(readOnly = true)
    public PageableResponse<MessageResponse> getMessages(MessageRequest messageRequest) {
        log.info("ActionLog.getMessages.Start");
        Page<MessageEntity> page = repository.findByChatIdAndUserIdOrderByMessageChatNAsc(
                messageRequest.getChatId(), messageRequest.getUserId(),
                PageRequest.of(messageRequest.getOffset() / messageRequest.getLimit(), messageRequest.getLimit()));
        var data = mapper.entityListToResponseList(page.getContent());
        log.info("ActionLog.getMessages.End");
        return new PageableResponse<MessageResponse>(data, page.getNumber(), page.getSize(), page.getTotalElements());
    }
}
