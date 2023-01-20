package com.aiforpet.tdogtdog.fcm.helper;

import com.aiforpet.tdogtdog.module.account.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface TestAccountRepository extends JpaRepository<Account, Long> {
    public Account findByEmail(String s);
    public void deleteAllInBatch();

    public List<Account> findAllByEmail(String email);
}
