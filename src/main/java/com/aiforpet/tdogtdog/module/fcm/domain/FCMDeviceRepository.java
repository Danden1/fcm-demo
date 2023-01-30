package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.account.Account;

import java.time.Instant;
import java.util.List;

public interface FCMDeviceRepository {
    FCMDevice findAllByDevice(String device);
    void deleteByDevice(String device);
    List<FCMDevice> findAllByRequestLocationAndAccountIn(RequestLocation requestLocation, List<Account> accounts);
    List<FCMDevice> findAllByAccount(Account account);
    void deleteByTimeLessThan(Instant time);
    void save(FCMDevice fcmDevice);
    NotificationSettings findNotificationByDevice(String device);
}
