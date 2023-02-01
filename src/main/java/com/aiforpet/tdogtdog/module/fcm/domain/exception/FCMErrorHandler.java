package com.aiforpet.tdogtdog.module.fcm.domain.exception;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;

public interface FCMErrorHandler {
    void handleError(FCMErrorType fcmExceptionType, Message message);
}
