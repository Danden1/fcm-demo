package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.fcm.domain.exception.InvalidMessageConstraintException;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MessageConstraint {
    private final NotificationType notificationType;
    private final LocalDateTime timeLimit;
    private final RequestLocation requestLocation;
    private final LocalDateTime reservationTime;

    public MessageConstraint(NotificationType notificationType, LocalDateTime timeLimit, RequestLocation requestLocation, LocalDateTime reservationTime) throws InvalidMessageConstraintException{
        this.notificationType = notificationType;
        this.timeLimit = timeLimit;
        this.requestLocation = requestLocation;
        this.reservationTime = reservationTime;
        if(!isValid()){
            throw new InvalidMessageConstraintException();
        }
    }

    private boolean isValid(){
        if(this.getRequestLocation() == null || this.getNotificationType() == null || this.getTimeLimit() == null || this.reservationTime == null){
            return false;
        }
        return true;
    }
}
