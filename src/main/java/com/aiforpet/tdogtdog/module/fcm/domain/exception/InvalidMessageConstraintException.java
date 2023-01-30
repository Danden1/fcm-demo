package com.aiforpet.tdogtdog.module.fcm.domain.exception;

public class InvalidMessageConstraintException extends InvalidMessageException{
    private final String message = MessageExceptionType.INVALID_MESSAGE_CONSTRAINT.getMessage();

    public String getMessage(){
        return message;
    }
}
