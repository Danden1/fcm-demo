package com.aiforpet.tdogtdog.module.fcm.domain;


import com.aiforpet.tdogtdog.module.account.Account;
import lombok.Getter;

@Getter
public class Receiver {
    private final String receiveDevice;
    private final DeviceType deviceType;

    public Receiver(String receiveDevice, DeviceType deviceType){
        this.receiveDevice = receiveDevice;
        this.deviceType = deviceType;

        if(!isValid()){
            throw new NullPointerException("receiver not allow null.");
        }
    }

    private boolean isValid(){
        if(this.getReceiveDevice() == null || this.getDeviceType() == null){
            return false;
        }
        return true;
    }
}
