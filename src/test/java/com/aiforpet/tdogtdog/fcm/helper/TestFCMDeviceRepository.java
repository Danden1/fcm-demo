package com.aiforpet.tdogtdog.fcm.helper;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDevice;
import com.aiforpet.tdogtdog.module.fcm.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestFCMDeviceRepository extends JpaRepository<FCMDevice, Long> {
    public void deleteAllInBatch();
    public  FCMDevice findByAccount(Account account);
    public  FCMDevice findByDevice(String device);

}
