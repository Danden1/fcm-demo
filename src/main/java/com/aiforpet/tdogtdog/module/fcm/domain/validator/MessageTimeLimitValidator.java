package com.aiforpet.tdogtdog.module.fcm.domain.validator;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.MessageConstraint;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.TimeLimitException;

import java.time.ZonedDateTime;

public class MessageTimeLimitValidator implements MessageValidator {

    @Override
    public void assertValidMessage(Message message) throws TimeLimitException {
        MessageConstraint messageConstraint = message.getMessageConstraint();
        ZonedDateTime timeLimit = messageConstraint.getTimeLimit();

        ZonedDateTime nowTime = ZonedDateTime.now(timeLimit.getZone());


        if(timeLimit.isBefore(nowTime)){
            throw new TimeLimitException();
        }
    }
}
