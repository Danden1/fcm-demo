package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAccountRepository  extends JpaRepository<Account, Long> {
    public Account findAccountById(long id);
}
