package com.aiforpet.tdogtdog.module.fcm.service;


import com.aiforpet.tdogtdog.module.fcm.dto.MessageForAccountDto;
import com.aiforpet.tdogtdog.module.fcm.dto.MessageForAllDto;


public interface SendMessageService {
    void sendToAllDevice(MessageForAllDto messageDto);
    void sendToDevice(MessageForAccountDto messageDto);
}
