package com.aiforpet.tdogtdog.module.fcm.domain.checker;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationType;
import com.aiforpet.tdogtdog.module.fcm.domain.RequestLocation;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class SendingTimeChecker implements ResendChecker{


    @Override
    public boolean isResend(Message message) {
        return isSendTimeOver(message.getRequestLocation());
    }

    private boolean isSendTimeOver(RequestLocation requestLocation){
        if(requestLocation == RequestLocation.KOREA){
            ZonedDateTime nowTime = ZonedDateTime.now(ZoneId.of("asia/seoul"));

            return !(8 <= nowTime.getHour() && nowTime.getHour() <= 20);
        }
        else if(requestLocation == RequestLocation.US){
            return true;
        }
        else if(requestLocation == RequestLocation.GERMANY){
            return true;
        }
        else if(requestLocation == RequestLocation.TEST_BETWEEN_TIME){
            return !ZonedDateTime.now().isBefore(ZonedDateTime.now().plus(5, ChronoUnit.MINUTES));
        }
        else if(requestLocation == RequestLocation.TEST_OVER_TIME){
            return !ZonedDateTime.now().isAfter(ZonedDateTime.now().plus(5, ChronoUnit.MINUTES));
        }

        return false;
    }
}
