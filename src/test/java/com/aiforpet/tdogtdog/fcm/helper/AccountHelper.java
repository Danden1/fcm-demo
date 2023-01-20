package com.aiforpet.tdogtdog.fcm.helper;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.Notification;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationSetting;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationType;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

@Component
public class AccountHelper {

    private final TestAccountRepository testAccountRepository;
    private final NotificationRepository notificationRepository;
    private final TestNotificationRepository testNotificationRepository;

    public AccountHelper(TestAccountRepository testAccountRepository, NotificationRepository notificationRepository, TestNotificationRepository testNotificationRepository) {
        this.testAccountRepository = testAccountRepository;
        this.notificationRepository = notificationRepository;
        this.testNotificationRepository = testNotificationRepository;
    }


    @Transactional
    public Account createAccount(String email){
        Account account = new Account();
        account.setEmail(email);


        Notification notification = new Notification(account);
        notification.updateNotification(NotificationType.TEST, NotificationSetting.ON);
        notification.updateNotification(NotificationType.EVENT, NotificationSetting.OFF);

        testAccountRepository.save(account);
        notificationRepository.save(notification);

        return account;
    }

    @Transactional
    public void deleteAccount(){
        testAccountRepository.deleteAll();
    }

}
