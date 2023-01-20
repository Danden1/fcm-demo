package com.aiforpet.tdogtdog.module.fcm.domain.exception;

public class TimeLimitException extends MessageException{
    private String messgae = MessageExceptionType.OVER_TIME_LIMIT.getMessage();

    @Override
    public String getMessage(){
        return messgae;
    }
}
