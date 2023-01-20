package com.aiforpet.tdogtdog.module.fcm.domain;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public enum RequestLocation {
    GERMANY{
        @Override
        public boolean isPushSendingTime(ZonedDateTime time){
            return true;
        }
    },
    KOREA{
        @Override
        public boolean isPushSendingTime(ZonedDateTime time){
            int hour = time.getHour();
            return 8 <= hour && hour <= 19;
        }

    },
    US{
        @Override
        public boolean isPushSendingTime(ZonedDateTime time){
            return true;
        }
    },
    TEST_BETWEEN_TIME{
        @Override
        public boolean isPushSendingTime(ZonedDateTime time) {
            ZonedDateTime startTime = ZonedDateTime.now(time.getZone()).minus(1, ChronoUnit.HOURS);
            ZonedDateTime endTime = ZonedDateTime.now(time.getZone()).plus(1, ChronoUnit.HOURS);

            return  (time.isBefore(endTime) && time.isAfter(startTime));
        }
    },
    TEST_OVER_TIME{
        @Override
        public boolean isPushSendingTime(ZonedDateTime time) {
            ZonedDateTime startTime = ZonedDateTime.now(time.getZone()).plus(1, ChronoUnit.HOURS);
            ZonedDateTime endTime = ZonedDateTime.now(time.getZone()).plus(2, ChronoUnit.HOURS);

            return  (time.isBefore(endTime) && time.isAfter(startTime));
        }
    };



    public abstract boolean isPushSendingTime(ZonedDateTime time);
}
