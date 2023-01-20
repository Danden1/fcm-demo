package com.aiforpet.tdogtdog.module.fcm.domain.validator;

import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.PushTimeException;

import java.time.ZonedDateTime;

public class MessagePushTimeValidator implements MessageValidator {

    @Override
    public void assertValidMessage(Message message) throws PushTimeException {
        MessageConstraint messageConstraint = message.getMessageConstraint();
        ZonedDateTime timeLimit = messageConstraint.getTimeLimit();
        RequestLocation requestLocation =messageConstraint.getRequestLocation();

        ZonedDateTime nowTime = ZonedDateTime.now(timeLimit.getZone());

        if(!requestLocation.isPushSendingTime(nowTime)){
            throw new PushTimeException();
        }
    }
}
