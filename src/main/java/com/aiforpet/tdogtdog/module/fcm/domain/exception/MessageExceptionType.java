package com.aiforpet.tdogtdog.module.fcm.domain.exception;

public enum MessageExceptionType {
    INVALID_MESSAGE("Invalid message field. Can't create message."),
    OVER_PUSH_TIME("Over push time. Retry in push time."),
    OVER_TIME_LIMIT("Over message's time limit. Delete message."),
    OFF_NOTIFICATION("Off notification. Can't send.");

    private String message;

    MessageExceptionType(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }

}
