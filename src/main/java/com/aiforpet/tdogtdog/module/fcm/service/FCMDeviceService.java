package com.aiforpet.tdogtdog.module.fcm.service;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.DeviceType;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationControl;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationType;
import com.aiforpet.tdogtdog.module.fcm.domain.RequestLocation;

import java.time.Instant;
import java.util.Map;

public interface FCMDeviceService {

    public String updateToken(String beforeDevice, String afterDevice);

    public Instant updateTime(String device);

    public Map<NotificationType, Boolean> updateAccountNotification(Account account, NotificationType notificationType, NotificationControl notificationSetting);

    public String createDevice(Account account, String Device, DeviceType deviceType, RequestLocation requestLocation);

    public RequestLocation updateRequestLocation(String device, RequestLocation requestLocation);
}
