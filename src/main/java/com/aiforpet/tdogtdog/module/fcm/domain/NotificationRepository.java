package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.account.Account;

import java.util.List;

public interface NotificationRepository {
    public NotificationSettings findByAccount(Account account);

    List<Account> findAccountIdByAvailableNotificationContains(NotificationType notificationType);
    void save(NotificationSettings notification);
}
