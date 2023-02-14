package com.aiforpet.tdogtdog.module.fcm.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum NotificationType {
    IMPORTANT,
    EVENT,
    CHAT,
    CARE,
    VIDEO_HEALTH_CHECK,
    TEST;

    @JsonCreator
    public static NotificationType from(String value){
        return NotificationType.valueOf(value.toUpperCase());
    }
}
