package com.aiforpet.tdogtdog.module.fcm.domain.exception;

public class NotificationException extends MessageException{

    private final String message = MessageExceptionType.OFF_NOTIFICATION.getMessage();

    @Override
    public String getMessage() {
        return message;
    }
}
