package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.account.Account;

import java.time.Instant;
import java.util.List;

public interface FCMDeviceRepository {
    public FCMDevice findAllByDevice(String device);
    public void deleteByDevice(String device);
    public List<FCMDevice> findAllByRequestLocationAndAccountIn(RequestLocation requestLocation, List<Account> accounts);
    public List<FCMDevice> findAllByAccount(Account account);
    public void deleteByTimeLessThan(Instant time);
    public void save(FCMDevice fcmDevice);
    public Notification findNotificationByDevice(String device);
}
