package com.aiforpet.tdogtdog.module.fcm.domain.validator;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.MessageException;

public interface MessageValidator {
    public void assertValidMessage(Message message) throws MessageException;
}
