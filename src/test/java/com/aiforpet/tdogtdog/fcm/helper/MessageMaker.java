package com.aiforpet.tdogtdog.fcm.helper;

import com.aiforpet.tdogtdog.module.fcm.domain.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class MessageMaker {

    public Message makeValidTestMessage(String token){
        MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, LocalDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_BETWEEN_TIME, LocalDateTime.now());
        Receiver receiver = new Receiver(token, DeviceType.IOS);
        Map<String, Object> data = new HashMap<>();
        data.put("hi", 123);

        return new Message("hi", "hi", data, receiver, constraint);
    }

    public Message makeEventMessage(String token){
        MessageConstraint constraint = new MessageConstraint(NotificationType.EVENT, LocalDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_BETWEEN_TIME, LocalDateTime.now());
        Receiver receiver = new Receiver(token, DeviceType.IOS);
        Map<String, Object> data = new HashMap<>();
        data.put("hi", 123);

        return new Message("hi", "hi", data, receiver, constraint);
    }


    public Message makeOverSendingTimeMessage(String token){
        MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, LocalDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_OVER_TIME, LocalDateTime.now());
        Receiver receiver = new Receiver(token, DeviceType.IOS);
        Map<String, Object> data = new HashMap<>();
        data.put("hi", 123);
        return new Message("hi", "hi", data, receiver, constraint);
    }

    public Message makeOverTimeLimitMessage(String token){
        MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, LocalDateTime.now().minus(1, ChronoUnit.HOURS), RequestLocation.TEST_BETWEEN_TIME, LocalDateTime.now());
        Receiver receiver = new Receiver(token, DeviceType.IOS);
        Map<String, Object> data = new HashMap<>();
        data.put("hi", 123);
        return new Message("hi", "hi", data, receiver, constraint);
    }

    public Message makeReservatinMessage(String token){
        MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, LocalDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_BETWEEN_TIME, LocalDateTime.now().plusMinutes(1));
        Receiver receiver = new Receiver(token, DeviceType.IOS);
        Map<String, Object> data = new HashMap<>();
        data.put("hi", 123);

        return new Message("hi", "hi", data, receiver, constraint);
    }
    public String getMessage(String token){
        return String.format("[title : hi, body : hi, data : {hi=123}, device : %s]", token);
    }
}
