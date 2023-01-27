package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.Messenger;
import com.aiforpet.tdogtdog.module.fcm.dto.HttpV1PushMessageDto;
import com.aiforpet.tdogtdog.module.fcm.dto.PushMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class MessengerImpl implements Messenger {

    private final HttpV1MessageMapper httpV1MessageMapper;
    private final ObjectMapper objectMapper;

    public MessengerImpl(HttpV1MessageMapper httpV1MessageMapper) {
        this.httpV1MessageMapper = httpV1MessageMapper;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void deliverMessage(Message message) {
        PushMessageDto httpV1MessageDto = httpV1MessageMapper.mapMessage(message);

        try {
            System.out.println(objectMapper.writeValueAsString(httpV1MessageDto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
