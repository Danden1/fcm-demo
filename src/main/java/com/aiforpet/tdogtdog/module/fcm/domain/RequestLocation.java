package com.aiforpet.tdogtdog.module.fcm.domain;

import java.time.ZoneId;

public enum RequestLocation {
    GERMANY(ZoneId.of("Europe/Berlin")),
    KOREA(ZoneId.of("Asia/Seoul")),
    US_LA(ZoneId.of("America/Los_Angeles")),
    TEST_BETWEEN_TIME(ZoneId.of("Asia/Seoul")),
    TEST_OVER_TIME(ZoneId.of("Asia/Seoul"));

    private final ZoneId timeZone;

    RequestLocation(ZoneId timeZone){
        this.timeZone = timeZone;
    }

    public ZoneId getTimeZone() {
        return timeZone;
    }
}
