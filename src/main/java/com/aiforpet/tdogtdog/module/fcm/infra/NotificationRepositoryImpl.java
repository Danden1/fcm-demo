package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.Notification;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationRepository;
import org.springframework.stereotype.Component;

@Component
public class NotificationRepositoryImpl implements NotificationRepository {

    private final JpaNotificationRepository jpaNotificationRepository;

    public NotificationRepositoryImpl(JpaNotificationRepository jpaNotificationRepository) {
        this.jpaNotificationRepository = jpaNotificationRepository;
    }

    @Override
    public Notification findByAccount(Account account) {
        return jpaNotificationRepository.findByAccount(account);
    }

    @Override
    public void save(Notification notification) {
        jpaNotificationRepository.save(notification);
    }


}
