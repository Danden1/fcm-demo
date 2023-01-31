package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.fcm.domain.exception.InvalidMessageConstraintException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Getter
public class MessageConstraint {
    private final NotificationType notificationType;
    private final LocalDateTime timeLimit;
    private final RequestLocation requestLocation;

    public MessageConstraint(NotificationType notificationType, LocalDateTime timeLimit, RequestLocation requestLocation) throws InvalidMessageConstraintException{
        this.notificationType = notificationType;
        this.timeLimit = timeLimit;
        this.requestLocation = requestLocation;
        if(!isValid()){
            throw new InvalidMessageConstraintException();
        }
    }

    private boolean isValid(){
        if(this.getRequestLocation() == null || this.getNotificationType() == null || this.getTimeLimit() == null){
            return false;
        }
        return true;
    }
}
