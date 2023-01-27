package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationSettings;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JpaNotificationRepository extends JpaRepository<NotificationSettings, Long> {
    public NotificationSettings findByAccount(Account account);
    public NotificationSettings findNotificationById(long id);

//    @Query(value = "select  distinct nt.account_id from Notification nt where ?1 member of nt.availableNotification", nativeQuery = true)
    List<NotificationSettings> findAllByAvailableNotificationContains(NotificationType notificationType);
}
