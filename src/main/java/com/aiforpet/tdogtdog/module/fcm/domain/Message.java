package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.fcm.domain.exception.InvalidMessageException;
import lombok.Getter;

import java.util.Map;

@Getter
public class Message {
    private String title;
    private String body;
    private Map<String, Object> data;
    private Receiver receiver;
    private MessageConstraint messageConstraint;

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

    private boolean isValidMessage(){
        if(this.title == null || this.body == null){
            return false;
        }
        if(this.receiver == null){
            return false;
        }
        if(this.receiver.getReceiveDevice() == null || this.receiver.getDeviceType() == null || this.receiver.getAccount() == null){
            return false;
        }
        if(this.messageConstraint == null){
            return false;
        }
        if(this.messageConstraint.getRequestLocation() == null || this.messageConstraint.getNotificationType() == null || this.getMessageConstraint().getTimeLimit() == null){
            return false;
        }

        return true;
    }

}
