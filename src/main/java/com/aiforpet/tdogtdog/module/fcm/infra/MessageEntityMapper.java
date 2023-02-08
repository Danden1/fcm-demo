package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.MessageConstraint;
import com.aiforpet.tdogtdog.module.fcm.domain.Receiver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MessageEntityMapper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    //추후에 builder 이용하여 refactoring 필요.
    public MessageEntity mapMessageToMessageEntity(Message message){
        MessageEntity messageEntity = new MessageEntity();
        Map<String, Object> data = message.getData();

        String stringData = null;
        try {
            stringData = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("MessageToMessageEntity Error", e);
        }

        messageEntity.setBody(message.getBody());
        messageEntity.setTitle(message.getTitle());
        messageEntity.setData(stringData);

        messageEntity.setTimeLimit(message.getTimeLimit());
        messageEntity.setNotificationType(message.getNotificationType());
        messageEntity.setRequestLocation(message.getRequestLocation());
        messageEntity.setRequestTime(message.getReservationTime());

        messageEntity.setReceiveDevice(message.getReceiveDevice());
        messageEntity.setDeviceType(message.getDeviceType());

        return messageEntity;
    }

    public Message mapMessageEntitytoMessage(MessageEntity messageEntity){
        Receiver receiver = new Receiver(messageEntity.getReceiveDevice(), messageEntity.getDeviceType());
        MessageConstraint messageConstraint = new MessageConstraint(messageEntity.getNotificationType(), messageEntity.getTimeLimit(), messageEntity.getRequestLocation(), messageEntity.getRequestTime());
        Map<String, Object> data;
        try {
            data = objectMapper.readValue(messageEntity.getData(), Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("MessageEntitytoMessage Error", e);
        }

        String title = messageEntity.getTitle();
        String body = messageEntity.getBody();

        return new Message(title, body, data, receiver, messageConstraint);
    }

}
