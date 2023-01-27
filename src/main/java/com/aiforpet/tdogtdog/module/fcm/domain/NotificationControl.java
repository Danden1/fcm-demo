package com.aiforpet.tdogtdog.module.fcm.domain;

public enum NotificationControl {
    ON(true),
    OFF(false);

    private final boolean value;

    NotificationControl(boolean value){
        this.value = value;
    }

    public boolean getValue(){
        return this.value;
    }
}
