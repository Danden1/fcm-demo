package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.account.Account;

import java.util.List;

public interface NotificationSettingsRepository {
    NotificationSettings findByAccount(Account account);

    void save(NotificationSettings notification);
}
