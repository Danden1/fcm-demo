package com.aiforpet.tdogtdog.module.fcm.domain.exception;

public enum MessageExceptionType {
    INVALID_MESSAGE("Invalid message field. Can't create message.");

    private final String message;

    MessageExceptionType(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }

}
