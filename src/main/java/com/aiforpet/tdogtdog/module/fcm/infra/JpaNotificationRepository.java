package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDevice;
import com.aiforpet.tdogtdog.module.fcm.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface JpaNotificationRepository extends JpaRepository<Notification, Long> {
    public Notification findByAccount(Account account);

    @Query(value = "select distinct nt.account_id from Notification nt where nt.important=true", nativeQuery = true)
    public List<Long> findDistinctAccountByImportant();

    @Query(value = "select distinct nt.account_id from Notification nt where nt.event=true", nativeQuery = true)
    public List<Long> findDistinctAccountByEvent();

    @Query(value = "select distinct nt.account_id from Notification nt where nt.test=true", nativeQuery = true)
    public List<Long> findDistinctAccountByTest();
}
