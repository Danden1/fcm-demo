package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDevice;
import com.aiforpet.tdogtdog.module.fcm.domain.RequestLocation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface JpaFCMDeviceRepository extends JpaRepository<FCMDevice, Long>{
    public FCMDevice findByDevice(String token);
    public List<FCMDevice> findAllByRequestLocationAndAccountIn(RequestLocation requestLocation, List<Account> accounts);
    public List<FCMDevice> findAllByAccount(Account account);
    public void deleteByDevice(String token);
    public void deleteByTimeLessThan(Instant time);
}
