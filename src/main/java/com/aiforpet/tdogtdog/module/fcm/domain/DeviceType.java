package com.aiforpet.tdogtdog.module.fcm.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum DeviceType {
    ANDROID,
    IOS;

    @JsonCreator
    public static DeviceType from(String value){
        return DeviceType.valueOf(value.toUpperCase());
    }
}
