package com.aiforpet.tdogtdog.module.fcm.domain;

import lombok.Getter;

import java.time.ZonedDateTime;

@Getter
public class MessageConstraint {
    private final NotificationType notificationType;
    private final ZonedDateTime timeLimit;
    private final RequestLocation requestLocation;

    public MessageConstraint(NotificationType notificationType, ZonedDateTime timeLimit, RequestLocation requestLocation){
        this.notificationType = notificationType;
        this.timeLimit = timeLimit;
        this.requestLocation = requestLocation;
        if(!isValid()){
            throw new NullPointerException("MessageConstraint not allow null");
        }
    }

    private boolean isValid(){
        if(this.getRequestLocation() == null || this.getNotificationType() == null || this.getTimeLimit() == null){
            return false;
        }
        return true;
    }
}
