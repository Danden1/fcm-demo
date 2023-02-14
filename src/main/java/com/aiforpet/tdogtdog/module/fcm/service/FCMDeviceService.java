package com.aiforpet.tdogtdog.module.fcm.service;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.DeviceType;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationControl;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationType;
import com.aiforpet.tdogtdog.module.fcm.domain.RequestLocation;

import java.time.Instant;
import java.util.Map;

public interface FCMDeviceService {

    String updateToken(String beforeDevice, String afterDevice);

    Instant updateTime(String device);

    Map<NotificationType, NotificationControl> updateNotificationSettings(Account account, NotificationType notificationType, NotificationControl notificationSetting);

    String createDevice(Account account, String Device, DeviceType deviceType, RequestLocation requestLocation);

    RequestLocation updateRequestLocation(String device, RequestLocation requestLocation);
}
