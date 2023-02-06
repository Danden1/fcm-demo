package com.aiforpet.tdogtdog.fcm.helper;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.DeviceType;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDevice;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationSettings;
import com.aiforpet.tdogtdog.module.fcm.domain.RequestLocation;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class FCMDeviceHelper {


    private final TestFCMDeviceRepository testfcmDeviceRepository;
    private final TestNotificationRepository testNotificationRepository;

    public FCMDeviceHelper(TestFCMDeviceRepository testfcmDeviceRepository, TestNotificationRepository testNotificationRepository) {
        this.testfcmDeviceRepository = testfcmDeviceRepository;
        this.testNotificationRepository = testNotificationRepository;
    }

    @Transactional
    public String createDevice(Account account, String device, DeviceType deviceType, RequestLocation requestLocation) {
        NotificationSettings notification = testNotificationRepository.findByAccount(account);
        FCMDevice fcmDevice = null;
        try {
            fcmDevice = new FCMDevice(account, device, deviceType, requestLocation, notification);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        testfcmDeviceRepository.save(fcmDevice);

        return fcmDevice.getDevice();
    }
}
