package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.fcm.domain.exception.InvalidMessageException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Map;

public class Message {
    private final String title;
    private final String body;
    private final Map<String, Object> data;
    private final Receiver receiver;
    private final MessageConstraint messageConstraint;

    public Message(String title, String body, Map<String, Object> data, Receiver receiver, MessageConstraint messageConstraint) throws InvalidMessageException{
        this.title = title;
        this.body = body;
        this.data = data;
        this.receiver = receiver;
        this.messageConstraint = messageConstraint;
        if (!isValid()){
            throw new InvalidMessageException();
        }
    }

    public LocalDateTime getTimeLimit(){
        return messageConstraint.getTimeLimit();
    }
    public LocalDateTime getRequestTime(){
        return messageConstraint.getRequestTime();
    }


    public String getReceiveDevice(){
        return receiver.getReceiveDevice();
    }
    public DeviceType getDeviceType(){
        return receiver.getDeviceType();
    }

    public String getTitle(){
        return title;
    }
    public String getBody(){
        return body;
    }
    public Map<String, Object> getData(){
        return data;
    }

    public NotificationType getNotificationType(){
        return messageConstraint.getNotificationType();
    }

    public RequestLocation getRequestLocation(){
        return messageConstraint.getRequestLocation();
    }

    private boolean isValid(){
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
