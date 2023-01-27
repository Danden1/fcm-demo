package com.aiforpet.tdogtdog.fcm.helper;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDevice;
import com.aiforpet.tdogtdog.module.fcm.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TestFCMDeviceRepository extends JpaRepository<FCMDevice, Long> {
    public void deleteAllInBatch();
    public  FCMDevice findByAccount(Account account);
    public  FCMDevice findByDevice(String device);

//    @Query(value = "select notification from FCMDevice where device=?1", nativeQuery = true)
    List<Long> findNotificationIdByDevice(String device);

}
