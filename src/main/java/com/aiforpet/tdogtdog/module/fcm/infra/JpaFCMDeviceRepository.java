package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.FCMDevice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface JpaFCMDeviceRepository extends JpaRepository<FCMDevice, Long>{
    public FCMDevice findAllByDevice(String token);
    public void deleteByDevice(String token);
    public void deleteByTimeLessThan(Instant time);
}
