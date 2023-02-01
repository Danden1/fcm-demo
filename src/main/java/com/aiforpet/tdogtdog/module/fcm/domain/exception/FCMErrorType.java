package com.aiforpet.tdogtdog.module.fcm.domain.exception;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;

public enum FCMErrorType {
    INVALID_TOKEN("Invalid Token."),
    //TOKEN REMOVE
    UNREGISTERED("Unregistered Token. Delete Token."),
    MESSAGE_TOO_BIG("Message Payload Too Big."),
    DEVICE_MESSAGE_RATE_EXCEEDED("Device Message Rate Exceeded. Wait a second."),
    TIMEOUT("FCM TimeOut. Wait a second"),
    INTERNAL_SERVER_ERROR("FCM Server Error. Wait a second."),
    INVALID_JSON("Invalid json.");

    private final String errorMessage;

    FCMErrorType(String errorMessage){
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage(Message message){
        return String.format("%s {\"title\":%s, \"body\":%s}, \"device\":%s", this.errorMessage,message.getTitle(), message.getBody(), message.getReceiveDevice());
    }
}
