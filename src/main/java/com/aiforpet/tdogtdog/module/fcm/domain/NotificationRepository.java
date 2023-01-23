package com.aiforpet.tdogtdog.module.fcm.domain;

import com.aiforpet.tdogtdog.module.account.Account;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository {
    public Notification findByAccount(Account account);


    public List<Account> findAccountByImportant();
    public List<Account> findAccountByEvent();
    public List<Account> findAccountByTest();
    public void save(Notification notification);
}
