package com.aiforpet.tdogtdog.module.fcm.service;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Service
public class FCMDeviceServiceImpl implements FCMDeviceService{

    private final FCMDeviceRepository fcmDeviceRepository;
    private final NotificationRepository notificationRepository;

    public FCMDeviceServiceImpl(FCMDeviceRepository fcmDeviceRepository, NotificationRepository notificationRepository) {
        this.fcmDeviceRepository = fcmDeviceRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    public String updateToken(String beforeDevice, String afterDevice) {
        FCMDevice fcmDevice = fcmDeviceRepository.findAllByDevice(beforeDevice);
        fcmDevice.updateDevice(afterDevice);

        return fcmDevice.getDevice();
    }

    @Override
    public Instant updateTime(String device) {
        FCMDevice fcmDevice = fcmDeviceRepository.findAllByDevice(device);
        fcmDevice.updateTime();

        return fcmDevice.getTime();
    }

    @Override
    public Map<NotificationType, Boolean> updateAccountNotification(Account account, NotificationType notificationType, NotificationSetting notificationSetting) {
        Notification notification = notificationRepository.findByAccount(account);

        notification.updateNotification(notificationType, notificationSetting);

        Map<NotificationType, Boolean> map = new HashMap<>();

        map.put(notificationType, notificationSetting.getValue());

        return map;
    }

    @Override
    public String createDevice(Account account, String device, DeviceType deviceType) {
        FCMDevice fcmDevice = new FCMDevice(account, device, deviceType);

        fcmDeviceRepository.save(fcmDevice);

        return fcmDevice.getDevice();
    }




}
