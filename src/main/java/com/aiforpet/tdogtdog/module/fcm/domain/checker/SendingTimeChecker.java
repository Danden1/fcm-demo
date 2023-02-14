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
        if(isIgnoreSendingTime(message.getNotificationType())){
            return false;
        }

        return isSendTimeOver(message.getRequestLocation());
    }

    private boolean isSendTimeOver(RequestLocation requestLocation){
        if(requestLocation == RequestLocation.KOREA){
            ZonedDateTime nowTime = ZonedDateTime.now(requestLocation.getTimeZone());

            return !(8 <= nowTime.getHour() && nowTime.getHour() < 17);
        }
        else if(requestLocation == RequestLocation.US_LA){
            ZonedDateTime nowTime = ZonedDateTime.now(requestLocation.getTimeZone());

            return !(8 <= nowTime.getHour() && nowTime.getHour() < 19);
        }
        else if(requestLocation == RequestLocation.GERMANY){
            ZonedDateTime nowTime = ZonedDateTime.now(requestLocation.getTimeZone());

            return !(8 <= nowTime.getHour() && nowTime.getHour() < 18);
        }
        else if(requestLocation == RequestLocation.TEST_BETWEEN_TIME){
            return !ZonedDateTime.now().isBefore(ZonedDateTime.now().plus(5, ChronoUnit.MINUTES));
        }
        else if(requestLocation == RequestLocation.TEST_OVER_TIME){
            return !ZonedDateTime.now().isAfter(ZonedDateTime.now().plus(5, ChronoUnit.MINUTES));
        }

        return false;
    }

    private boolean isIgnoreSendingTime(NotificationType notificationType){
        if(notificationType == NotificationType.VIDEO_HEALTH_CHECK) {
            return true;
        }

        return false;
    }
}
