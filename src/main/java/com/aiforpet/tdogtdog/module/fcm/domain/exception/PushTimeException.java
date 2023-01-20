package com.aiforpet.tdogtdog.module.fcm.domain.exception;

public class PushTimeException extends MessageException{
    private String message = MessageExceptionType.OVER_PUSH_TIME.getMessage();

    @Override
    public String getMessage(){
        return message;
    }
}
