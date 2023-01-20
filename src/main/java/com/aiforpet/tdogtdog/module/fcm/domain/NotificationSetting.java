package com.aiforpet.tdogtdog.module.fcm.domain;

public enum NotificationSetting {
    ON(true),
    OFF(false);

    private final boolean value;

    NotificationSetting(boolean value){
        this.value = value;
    }

    public boolean getValue(){
        return this.value;
    }
}
