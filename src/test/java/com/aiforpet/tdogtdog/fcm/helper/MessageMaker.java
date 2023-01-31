package com.aiforpet.tdogtdog.fcm.helper;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

public class MessageMaker {

    public Message makeValidTestMessage(String token){
        MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, LocalDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_BETWEEN_TIME);
        Receiver receiver = new Receiver(token, DeviceType.IOS);
        Map<String, Object> data = new HashMap<>();
        data.put("hi", 123);

        return new Message("hi", "hi", data, receiver, constraint);
    }

    public Message makeEventMessage(String token){
        MessageConstraint constraint = new MessageConstraint(NotificationType.EVENT, LocalDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_BETWEEN_TIME);
        Receiver receiver = new Receiver(token, DeviceType.IOS);
        Map<String, Object> data = new HashMap<>();
        data.put("hi", 123);

        return new Message("hi", "hi", data, receiver, constraint);
    }


    public Message makeOverSendingTimeMessage(String token){
        MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, LocalDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_OVER_TIME);
        Receiver receiver = new Receiver(token, DeviceType.IOS);
        Map<String, Object> data = new HashMap<>();
        data.put("hi", 123);
        return new Message("hi", "hi", data, receiver, constraint);
    }

    public Message makeOverTimeLimitMessage(String token){
        MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, LocalDateTime.now().minus(1, ChronoUnit.HOURS), RequestLocation.TEST_BETWEEN_TIME);
        Receiver receiver = new Receiver(token, DeviceType.IOS);
        Map<String, Object> data = new HashMap<>();
        data.put("hi", 123);
        return new Message("hi", "hi", data, receiver, constraint);
    }

//    public String getPushMessage(String token){
//        return String.format("{\"message\":{\"notification\":{\"title\":\"hi\",\"body\":\"hi\"},\"token\":\"%s\",\"data\":{\"hi\":123}}}", token);
//    }
    public String getPushMessage(String token){
        return String.format("{\"to\":\"%s\",\"notification\":{\"title\":\"hi\",\"body\":\"hi\"},\"data\":{\"hi\":123}}", token);
    }
}
