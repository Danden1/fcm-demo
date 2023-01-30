package com.aiforpet.tdogtdog.module.fcm.domain.exception;

public class InvalidReceiverException extends InvalidMessageException{
    private final String message = MessageExceptionType.INVALID_RECEIVER.getMessage();

    public String getMessage(){
        return message;
    }
}
