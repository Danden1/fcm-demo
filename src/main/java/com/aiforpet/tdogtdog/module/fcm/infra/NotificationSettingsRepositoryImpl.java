package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationSettings;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationSettingsRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class NotificationSettingsRepositoryImpl implements NotificationSettingsRepository {

    private final JpaNotificationSettingsRepository jpaNotificationRepository;

    public NotificationSettingsRepositoryImpl(JpaNotificationSettingsRepository jpaNotificationRepository) {
        this.jpaNotificationRepository = jpaNotificationRepository;
    }

    @Override
    public NotificationSettings findByAccount(Account account) {
        return jpaNotificationRepository.findByAccount(account);
    }

    @Override
    public void save(NotificationSettings notification) {

        jpaNotificationRepository.save(notification);
    }


}
