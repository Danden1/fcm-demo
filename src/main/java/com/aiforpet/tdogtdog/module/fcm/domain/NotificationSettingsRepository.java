package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.account.Account;

import java.util.List;

public interface NotificationSettingsRepository {
    NotificationSettings findByAccount(Account account);

    List<Account> findAccountByAvailableNotificationContains(NotificationType notificationType);
    void save(NotificationSettings notification);
}
