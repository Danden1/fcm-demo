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
    private final NotificationSettingsRepository notificationRepository;

    public FCMDeviceServiceImpl(FCMDeviceRepository fcmDeviceRepository, NotificationSettingsRepository notificationRepository) {
        this.fcmDeviceRepository = fcmDeviceRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional
    public String updateToken(String beforeDevice, String afterDevice) {
        FCMDevice fcmDevice = fcmDeviceRepository.findAllByDevice(beforeDevice);

        fcmDevice.updateDevice(afterDevice);
        fcmDeviceRepository.save(fcmDevice);

        return fcmDevice.getDevice();
    }

    @Override
    @Transactional
    public Instant updateTime(String device) {
        FCMDevice fcmDevice = fcmDeviceRepository.findAllByDevice(device);
        fcmDevice.updateTime();

        return fcmDevice.getTime();
    }

    @Override
    @Transactional
    public Map<NotificationType, NotificationControl> updateAccountNotification(Account account, NotificationType notificationType, NotificationControl notificationControl) {
        NotificationSettings notification = notificationRepository.findByAccount(account);

        notification.updateNotification(notificationType, notificationControl);

        notificationRepository.save(notification);

        Map<NotificationType, NotificationControl> map = new HashMap<>();

        map.put(notificationType, notificationControl);

        return map;
    }

    @Override
    @Transactional
    public String createDevice(Account account, String device, DeviceType deviceType, RequestLocation requestLocation) {
        NotificationSettings notification = notificationRepository.findByAccount(account);
        FCMDevice fcmDevice = null;
        try {
            fcmDevice = new FCMDevice(account, device, deviceType, requestLocation, notification);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        fcmDeviceRepository.save(fcmDevice);

        return fcmDevice.getDevice();
    }

    @Override
    @Transactional
    public RequestLocation updateRequestLocation(String device, RequestLocation requestLocation){
        FCMDevice fcmDevice = fcmDeviceRepository.findAllByDevice(device);
        fcmDevice.updateRequestLocation(requestLocation);

        fcmDeviceRepository.save(fcmDevice);

        return fcmDevice.getRequestLocation();
    }


}
