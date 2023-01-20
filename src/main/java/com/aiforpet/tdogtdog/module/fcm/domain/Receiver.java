package com.aiforpet.tdogtdog.module.fcm.domain;


import com.aiforpet.tdogtdog.module.account.Account;
import lombok.Getter;

@Getter
public class Receiver {
    private final String receiveDevice;
    private final DeviceType deviceType;
    private final Account account;

    public Receiver(String receiveDevice, DeviceType deviceType, Account account){
        this.receiveDevice = receiveDevice;
        this.deviceType = deviceType;
        this.account = account;
    }
}
