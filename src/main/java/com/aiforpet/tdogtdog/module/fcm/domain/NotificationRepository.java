package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.account.Account;

public interface NotificationRepository {
    public Notification findByAccount(Account account);
    public void save(Notification notification);
}
