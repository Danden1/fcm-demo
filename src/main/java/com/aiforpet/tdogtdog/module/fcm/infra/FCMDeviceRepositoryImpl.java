package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.FCMDevice;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDeviceRepository;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class FCMDeviceRepositoryImpl implements FCMDeviceRepository {

    private final JpaFCMDeviceRepository jpaFCMDeviceRepository;

    public FCMDeviceRepositoryImpl(JpaFCMDeviceRepository jpaFCMDeviceRepository) {
        this.jpaFCMDeviceRepository = jpaFCMDeviceRepository;
    }

    @Override
    public FCMDevice findAllByDevice(String device) {

        return jpaFCMDeviceRepository.findAllByDevice(device);
    }

    @Override
    public void deleteByDevice(String device) {
        jpaFCMDeviceRepository.deleteByDevice(device);
    }

    @Override
    public void deleteByTimeLessThan(Instant time) {
        jpaFCMDeviceRepository.deleteByTimeLessThan(time);
    }


    @Override
    public void save(FCMDevice fcmDevice) {
        jpaFCMDeviceRepository.save(fcmDevice);
    }
}
