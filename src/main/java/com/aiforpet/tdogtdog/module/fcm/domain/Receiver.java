package com.aiforpet.tdogtdog.module.fcm.domain;


import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.InvalidReceiverException;
import lombok.Getter;

@Getter
public class Receiver {
    private final String receiveDevice;
    private final DeviceType deviceType;

    public Receiver(String receiveDevice, DeviceType deviceType) throws InvalidReceiverException{
        this.receiveDevice = receiveDevice;
        this.deviceType = deviceType;

        if(!isValid()){
            throw new InvalidReceiverException();
        }
    }

    private boolean isValid(){
        if(this.getReceiveDevice() == null || this.getDeviceType() == null){
            return false;
        }
        return true;
    }
}
