package com.aiforpet.tdogtdog.module.fcm.domain.exception;

public class InvalidMessageException extends MessageException{
    private final String message = MessageExceptionType.INVALID_MESSAGE.getMessage();

    public String getMessage(){
        return message;
    }
}
