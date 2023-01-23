package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.Notification;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationRepositoryImpl implements NotificationRepository {

    private final JpaNotificationRepository jpaNotificationRepository;
    private final JpaAccountRepository jpaAccountRepository;

    public NotificationRepositoryImpl(JpaNotificationRepository jpaNotificationRepository, JpaAccountRepository jpaAccountRepository) {
        this.jpaNotificationRepository = jpaNotificationRepository;
        this.jpaAccountRepository = jpaAccountRepository;
    }

    @Override
    public Notification findByAccount(Account account) {
        return jpaNotificationRepository.findByAccount(account);
    }

    @Override
    public void save(Notification notification) {
        jpaNotificationRepository.save(notification);
    }

    @Override
    public List<Account> findAccountByImportant(){
        List<Long> accountIds = jpaNotificationRepository.findDistinctAccountByImportant();


        return accountIds.stream().map(jpaAccountRepository::findAccountById).collect(Collectors.toList());
    }

    @Override
    public List<Account> findAccountByEvent(){
        List<Long> accountIds = jpaNotificationRepository.findDistinctAccountByEvent();

        return accountIds.stream().map(jpaAccountRepository::findAccountById).collect(Collectors.toList());
    }

    @Override
    public List<Account> findAccountByTest(){
        List<Long> accountIds = jpaNotificationRepository.findDistinctAccountByTest();

        return accountIds.stream().map(jpaAccountRepository::findAccountById).collect(Collectors.toList());
    }




}
