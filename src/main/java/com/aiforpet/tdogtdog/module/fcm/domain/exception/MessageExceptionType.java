package com.aiforpet.tdogtdog.module.fcm.domain.exception;

public enum MessageExceptionType {
    INVALID_MESSAGE("Invalid Message field. Can't create Message."),
    INVALID_RECEIVER("Invalid Receiver field. Can't create Receiver."),
    INVALID_MESSAGE_CONSTRAINT("Invalid MessageConstraint field. Can't create MessageConstraint.");

    private final String message;

    MessageExceptionType(String message){
        this.message = message;
    }

    public String getMessage(){
        return this.message;
    }

}
