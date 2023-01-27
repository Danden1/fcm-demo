package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.fcm.domain.exception.InvalidMessageException;
import lombok.Getter;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

@Getter
public class Message {
    private final String title;
    private final String body;
    private final Map<String, Object> data;
    private final Receiver receiver;
    private final MessageConstraint messageConstraint;

    public Message(String title, String body, Map<String, Object> data, Receiver receiver, MessageConstraint messageConstraint){
        this.title = title;
        this.body = body;
        this.data = data;
        this.receiver = receiver;
        this.messageConstraint = messageConstraint;
        if (!isValidMessage()){
            throw new InvalidMessageException();
        }
    }

    public ZonedDateTime getTimeLimit(){
        return messageConstraint.getTimeLimit();
    }


    public String getReceiveDevice(){
        return receiver.getReceiveDevice();
    }

    public NotificationType getNotificationType(){
        return messageConstraint.getNotificationType();
    }

    public RequestLocation getRequestLocation(){
        return messageConstraint.getRequestLocation();
    }

    private boolean isValidMessage(){
        if(this.title == null || this.body == null){
            return false;
        }
        if(this.receiver == null){
            return false;
        }

        if(this.messageConstraint == null) {
            return false;
        }
        return true;
    }

}
