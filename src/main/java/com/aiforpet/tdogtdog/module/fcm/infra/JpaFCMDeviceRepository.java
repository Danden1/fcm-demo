package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDevice;
import com.aiforpet.tdogtdog.module.fcm.domain.Notification;
import com.aiforpet.tdogtdog.module.fcm.domain.RequestLocation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface JpaFCMDeviceRepository extends JpaRepository<FCMDevice, Long>{

    FCMDevice findByDevice(String token);
    List<FCMDevice> findAllByRequestLocationAndAccountIn(RequestLocation requestLocation, List<Account> accounts);
    List<FCMDevice> findAllByAccount(Account account);
    void deleteByDevice(String token);
    void deleteByTimeLessThan(Instant time);

    @Query(value = "select notification_id from FCMDevice where device=?1", nativeQuery = true)
    Long findNotificationIdByDevice(String device);
}
