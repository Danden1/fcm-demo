package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.MessageBox;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;


@Component
public class DBMessageBox implements MessageBox {


    private final MessageBoxRepository messageBoxRepository;
    private final MessageEntityMapper messageEntityMapper;

    public DBMessageBox(MessageBoxRepository messageBoxRepository, MessageEntityMapper messageEntityMapper){
        this.messageBoxRepository = messageBoxRepository;
        this.messageEntityMapper = messageEntityMapper;
    }

    @Override
    @Transactional
    public void collectMessage(Message message) {

        MessageEntity messageEntity = messageEntityMapper.mapMessageToMessageEntity(message);

        messageBoxRepository.save(messageEntity);
    }



}
