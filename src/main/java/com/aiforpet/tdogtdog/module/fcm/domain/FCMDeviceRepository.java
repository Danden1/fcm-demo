package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.account.Account;

import java.time.Instant;
import java.util.List;

public interface FCMDeviceRepository {
    FCMDevice findByDevice(String device);
    void deleteByDevice(String device);

    List<FCMDevice> findAllByAccount(Account account);

    List<FCMDevice> findAllByRequestLocationAndNotificationSettings_AvailableNotificationContains(RequestLocation requestLocation, NotificationType notificationType);

    void deleteByTimeLessThan(Instant time);
    void save(FCMDevice fcmDevice);
    NotificationSettings findNotificationSettingsByDevice(String device);
}
