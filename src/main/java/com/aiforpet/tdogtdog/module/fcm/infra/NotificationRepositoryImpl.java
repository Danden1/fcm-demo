package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationSettings;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationType;
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
    public NotificationSettings findByAccount(Account account) {
        return jpaNotificationRepository.findByAccount(account);
    }

    @Override
    public void save(NotificationSettings notification) {

        jpaNotificationRepository.save(notification);
    }

    @Override
    public List<Account> findAccountIdByAvailableNotificationContains(NotificationType notificationType) {
        List<NotificationSettings> notifications = jpaNotificationRepository.findAllByAvailableNotificationContains(notificationType);
        return notifications.stream().map(NotificationSettings::getAccount).collect(Collectors.toList());
    }


}
