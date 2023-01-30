package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationSettings;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaNotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
    NotificationSettings findByAccount(Account account);
    NotificationSettings findNotificationById(long id);
}
