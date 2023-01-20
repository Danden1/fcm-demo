package com.aiforpet.tdogtdog.module.fcm.domain.validator;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.Notification;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.Receiver;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.MessageException;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.NotificationException;

public class MessageNotificationValidator implements MessageValidator{


    private final NotificationRepository notificationRepository;

    public MessageNotificationValidator(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }


    @Override
    public void assertValidMessage(Message message) throws MessageException {
        Receiver receiver = message.getReceiver();
        Account account = receiver.getAccount();

        Notification notification = notificationRepository.findByAccount(account);

        if(!notification.isNotification(message.getMessageConstraint().getNotificationType())){
            throw new NotificationException();
        }
    }
}
