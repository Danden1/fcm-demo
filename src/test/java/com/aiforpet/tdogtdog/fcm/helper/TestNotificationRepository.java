package com.aiforpet.tdogtdog.fcm.helper;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestNotificationRepository extends JpaRepository<NotificationSettings, Long> {
    public void deleteAllInBatch();
    public NotificationSettings findByAccount(Account account);
}
