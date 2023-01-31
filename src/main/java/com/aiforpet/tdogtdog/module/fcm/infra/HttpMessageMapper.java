package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.MessageMapper;
import com.aiforpet.tdogtdog.module.fcm.dto.*;
import org.springframework.stereotype.Component;

@Component
public class HttpMessageMapper implements MessageMapper {
    @Override
    public PushMessageDto mapMessage(Message message) {
        HttpPushMessageDto httpPushMessageDto = new HttpPushMessageDto();
        NotificationDto notificationDto = new NotificationDto();

        notificationDto.setTitle(message.getTitle());
        notificationDto.setBody(message.getBody());
        httpPushMessageDto.setData(message.getData());

        httpPushMessageDto.setTo(message.getReceiveDevice());

        httpPushMessageDto.setNotification(notificationDto);

        return httpPushMessageDto;
    }
}
