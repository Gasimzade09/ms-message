package com.rockup.app.mapper;

import com.rockup.app.model.entity.MessageEntity;
import com.rockup.app.model.request.MessageRequest;
import com.rockup.app.model.response.MessageResponse;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", imports = { java.util.UUID.class })
public interface MessageMapper {

    MessageResponse entityToResponse(MessageEntity entity);

    List<MessageResponse> entityListToResponseList(List<MessageEntity> entities);

    @Mapping(target = "id", expression = "java(UUID.randomUUID())")
    MessageEntity requestToEntity(MessageRequest request, Integer messageChatN);
}
