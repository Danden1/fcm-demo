package com.aiforpet.tdogtdog.module.fcm.domain.checker;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;

public interface DestroyChecker {
    boolean isDestroy(Message message);
}
