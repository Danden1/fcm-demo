package com.aiforpet.tdogtdog.module.fcm.service;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;


@Service
@EnableAsync
public class SendMessageServiceImpl implements SendMessageService {

    private final MessageBox messageBox;
    private final FCMDeviceRepository fcmDeviceRepository;
    private final NotificationRepository notificationRepository;

    public SendMessageServiceImpl(MessageBox messageBox, FCMDeviceRepository fcmDeviceRepository, NotificationRepository notificationRepository) {
        this.messageBox = messageBox;
        this.fcmDeviceRepository = fcmDeviceRepository;
        this.notificationRepository = notificationRepository;
    }


    @Override
    @Transactional
    @Async
    public void sendToAllDevice(NotificationType notificationType, String body, String title, Map<String, Object> data, RequestLocation requestLocation, ZonedDateTime timeLimit) {
        List<Account> accounts = null;

        if(notificationType == NotificationType.IMPORTANT)
            accounts = notificationRepository.findAccountByImportant();
        if(notificationType == NotificationType.TEST)
            accounts = notificationRepository.findAccountByTest();
        if(notificationType == NotificationType.EVENT)
            accounts = notificationRepository.findAccountByEvent();

        List<FCMDevice>  fcmDevices = fcmDeviceRepository.findAllByRequestLocationAndAccountIn(requestLocation, accounts);

        for(FCMDevice fcmDevice : fcmDevices){
            Receiver receiver = new Receiver(fcmDevice.getDevice(), fcmDevice.getDeviceType(), fcmDevice.getAccount());
            MessageConstraint messageConstraint = new MessageConstraint(notificationType, timeLimit, requestLocation);
            Message message = new Message(title, body, data, receiver, messageConstraint);

            messageBox.collectMessage(message);
        }
    }

    @Override
    @Transactional
    @Async
    public void sendToDevice(Account account, NotificationType notificationType, String body, String title, Map<String, Object> data, RequestLocation requestLocation, ZonedDateTime timeLimit) {
        Notification notification = notificationRepository.findByAccount(account);

        if(notificationType == NotificationType.IMPORTANT && !notification.getImportant())
            return;
        if(notificationType == NotificationType.TEST && !notification.getTest())
            return;
        if(notificationType == NotificationType.EVENT && !notification.getEvent())
            return;

        List<FCMDevice>  fcmDevices = fcmDeviceRepository.findAllByAccount(account);

        for(FCMDevice fcmDevice : fcmDevices){
            Receiver receiver = new Receiver(fcmDevice.getDevice(), fcmDevice.getDeviceType(), fcmDevice.getAccount());
            MessageConstraint messageConstraint = new MessageConstraint(notificationType, timeLimit, requestLocation);
            Message message = new Message(title, body, data, receiver, messageConstraint);

            messageBox.collectMessage(message);
        }
    }
}
