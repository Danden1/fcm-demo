package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.MessageMapper;
import com.aiforpet.tdogtdog.module.fcm.dto.HttpV1MessageDto;
import com.aiforpet.tdogtdog.module.fcm.dto.HttpV1PushMessageDto;
import com.aiforpet.tdogtdog.module.fcm.dto.NotificationDto;
import com.aiforpet.tdogtdog.module.fcm.dto.PushMessageDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;

@Component
public class HttpV1MessageMapper implements MessageMapper {

    @Override
    public PushMessageDto mapMessage(Message message) {
        HttpV1PushMessageDto httpV1PushMessageDto = new HttpV1PushMessageDto();
        HttpV1MessageDto httpV1MessageDto = new HttpV1MessageDto();
        NotificationDto notificationDto = new NotificationDto();

        notificationDto.setTitle(message.getTitle());
        notificationDto.setBody(message.getBody());
        httpV1MessageDto.setNotification(notificationDto);

        httpV1MessageDto.setData(message.getData());
        httpV1MessageDto.setToken(message.getReceiveDevice());


        httpV1PushMessageDto.setMessage(httpV1MessageDto);


        return httpV1PushMessageDto;
    }
}
