package com.rockup.app.model.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rockup.app.model.type.MessageType;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageRequest {
    MessageType type;
    UUID chatId;
    UUID userId;
    UUID messageId;
    Integer version;
    String payload;
    Integer limit;
    Integer offset;
}
