package com.aiforpet.tdogtdog.fcm.helper;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestNotificationRepository extends JpaRepository<Notification, Long> {
    public void deleteAllInBatch();
    public Notification findByAccount(Account account);
}
