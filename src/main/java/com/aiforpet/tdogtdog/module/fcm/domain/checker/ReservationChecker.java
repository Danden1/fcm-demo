package com.aiforpet.tdogtdog.module.fcm.domain.checker;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class ReservationChecker implements ResendChecker{
    @Override
    public boolean isResend(Message message) {
        LocalDateTime requestTime = message.getReservationTime();
        ZoneId timeZone = message.getRequestLocation().getTimeZone();
        ZonedDateTime nowTime = ZonedDateTime.now(timeZone);

        return nowTime.isBefore(ZonedDateTime.of(requestTime, timeZone));
    }
}
