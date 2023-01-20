package com.aiforpet.tdogtdog.fcm.helper;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.DeviceType;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDevice;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class FCMDeviceHelper {


    private final TestFCMDeviceRepository testfcmDeviceRepository;

    public FCMDeviceHelper(TestFCMDeviceRepository testfcmDeviceRepository) {
        this.testfcmDeviceRepository = testfcmDeviceRepository;
    }

    @Transactional
    public String createDevice(Account account, String device, DeviceType deviceType) {
        FCMDevice fcmDevice = new FCMDevice(account, device, deviceType);

        testfcmDeviceRepository.save(fcmDevice);

        return fcmDevice.getDevice();
    }
}
