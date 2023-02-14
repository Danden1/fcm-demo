package com.aiforpet.tdogtdog.module.fcm.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum NotificationControl {
    ON,OFF;

    @JsonCreator
    public static NotificationControl from(String value){
        return NotificationControl.valueOf(value.toUpperCase());
    }
}
