package com.aiforpet.tdogtdog.module.fcm.domain.checker;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;

import java.time.ZoneId;
import java.time.ZonedDateTime;

public class TimeLimitChecker implements DestroyChecker {
    @Override
    public boolean isDestroy(Message message) {
        ZonedDateTime timeLimit = message.getTimeLimit();
        ZoneId requestZone = timeLimit.getZone();
        //data?

        ZonedDateTime nowTime = ZonedDateTime.now(requestZone);


        return nowTime.isAfter(timeLimit);
    }
}
