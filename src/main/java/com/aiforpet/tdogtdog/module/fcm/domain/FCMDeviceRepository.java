package com.aiforpet.tdogtdog.module.fcm.domain;

import java.time.Instant;
import java.util.List;

public interface FCMDeviceRepository {
    public FCMDevice findAllByDevice(String device);
    public void deleteByDevice(String device);
    public void deleteByTimeLessThan(Instant time);
    public void save(FCMDevice fcmDevice);
}
