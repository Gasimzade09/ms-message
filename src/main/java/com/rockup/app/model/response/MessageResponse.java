package com.rockup.app.model.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.rockup.app.model.type.MessageStatus;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MessageResponse {
    MessageStatus status;
    String payload;
    UUID id;
    Integer messageChatN;
    Integer version;
}
