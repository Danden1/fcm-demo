package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.DeviceScheduler;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDeviceRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@Component
public class DeviceSchedulerImpl implements DeviceScheduler {

    private final FCMDeviceRepository fcmDeviceRepository;

    public DeviceSchedulerImpl(FCMDeviceRepository fcmDeviceRepository) {
        this.fcmDeviceRepository = fcmDeviceRepository;
    }

    @Override
    @Scheduled(cron = "0 0 3 20 * ?")
    @Transactional
    public void deleteOldDevice() {
        fcmDeviceRepository.deleteByTimeLessThan(Instant.now().minus(60, ChronoUnit.DAYS));
    }
}
