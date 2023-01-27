package com.aiforpet.tdogtdog.module.fcm.domain.checker;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;

public interface ResendChecker {
    boolean isResend(Message message);
}
