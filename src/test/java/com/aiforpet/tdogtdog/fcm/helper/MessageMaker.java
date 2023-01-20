package com.aiforpet.tdogtdog.fcm.helper;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class MessageMaker {

    public List<Message> makeValidTestMessages(int numberOfMessage, Account account){
        MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_BETWEEN_TIME);
        Receiver receiver = new Receiver("123", DeviceType.IOS, account);
        List<Message> messages = new ArrayList<>();

        for(int i = 0; i < numberOfMessage; i++){
            messages.add(new Message("hi", "hi", null, receiver, constraint));
        }

        return messages;
    }

    public List<Message> makeEventMessages(int numberOfMessage, Account account){
        MessageConstraint constraint = new MessageConstraint(NotificationType.EVENT, ZonedDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_BETWEEN_TIME);
        Receiver receiver = new Receiver("123", DeviceType.IOS, account);
        List<Message> messages = new ArrayList<>();

        for(int i = 0; i < numberOfMessage; i++){
            messages.add(new Message("hi", "hi", null, receiver, constraint));
        }

        return messages;
    }

    public List<Message> makeOverSendingTimeAndEventMessages(int numberOfMessage, Account account){
        MessageConstraint constraint = new MessageConstraint(NotificationType.EVENT, ZonedDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_OVER_TIME);
        Receiver receiver = new Receiver("123", DeviceType.IOS, account);
        List<Message> messages = new ArrayList<>();

        for(int i = 0; i < numberOfMessage; i++){
            messages.add(new Message("hi", "hi", null, receiver, constraint));
        }

        return messages;
    }

    public List<Message> makeOverSendingTimeMessages(int numberOfMessage, Account account){
        MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now().plus(1, ChronoUnit.HOURS), RequestLocation.TEST_OVER_TIME);
        Receiver receiver = new Receiver("123", DeviceType.IOS, account);
        List<Message> messages = new ArrayList<>();

        for(int i = 0; i < numberOfMessage; i++){
            messages.add(new Message("hi", "hi", null, receiver, constraint));
        }

        return messages;
    }

    public List<Message> makeOverTimeLimitMessages(int numberOfMessage, Account account){
        MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now().minus(1, ChronoUnit.HOURS), RequestLocation.TEST_BETWEEN_TIME);
        Receiver receiver = new Receiver("123", DeviceType.IOS, account);
        List<Message> messages = new ArrayList<>();

        for(int i = 0; i < numberOfMessage; i++){
            messages.add(new Message("hi", "hi", null, receiver, constraint));
        }

        return messages;
    }
}
