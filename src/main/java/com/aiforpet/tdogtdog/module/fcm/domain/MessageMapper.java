package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.fcm.dto.PushMessageDto;

public interface MessageMapper {
    public PushMessageDto mapMessage(Message message);
}
