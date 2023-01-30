package com.aiforpet.tdogtdog.fcm.helper;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class MessageMaker {

    public Message makeValidTestMessage(String token){
        MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_BETWEEN_TIME);
        Receiver receiver = new Receiver(token, DeviceType.IOS);

        return new Message("hi", "hi", null, receiver, constraint);
    }

    public Message makeEventMessage(String token){
        MessageConstraint constraint = new MessageConstraint(NotificationType.EVENT, ZonedDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_BETWEEN_TIME);
        Receiver receiver = new Receiver(token, DeviceType.IOS);

        return new Message("hi", "hi", null, receiver, constraint);
    }


    public Message makeOverSendingTimeMessage(String token){
        MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_OVER_TIME);
        Receiver receiver = new Receiver(token, DeviceType.IOS);

        return new Message("hi", "hi", null, receiver, constraint);
    }

    public Message makeOverTimeLimitMessage(String token){
        MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now().minus(1, ChronoUnit.HOURS), RequestLocation.TEST_BETWEEN_TIME);
        Receiver receiver = new Receiver(token, DeviceType.IOS);

        return new Message("hi", "hi", null, receiver, constraint);
    }

    public String getPushMessage(String token){
        return String.format("{\"message\":{\"notification\":{\"title\":\"hi\",\"body\":\"hi\"},\"token\":\"%s\",\"data\":null}}", token);
    }
}
