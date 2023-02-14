package com.aiforpet.tdogtdog.module.fcm.service;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.account.AccountRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.dto.MessageForAccountDto;
import com.aiforpet.tdogtdog.module.fcm.dto.MessageForAllDto;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;


@Service
@EnableAsync
public class SendMessageServiceImpl implements SendMessageService {

    private final MessageBox messageBox;
    private final FCMDeviceRepository fcmDeviceRepository;
    private final NotificationSettingsRepository notificationRepository;
    private final AccountRepository accountRepository;

    public SendMessageServiceImpl(MessageBox messageBox, FCMDeviceRepository fcmDeviceRepository, NotificationSettingsRepository notificationRepository, AccountRepository accountRepository) {
        this.messageBox = messageBox;
        this.fcmDeviceRepository = fcmDeviceRepository;
        this.notificationRepository = notificationRepository;
        this.accountRepository = accountRepository;
    }


    @Override
    @Transactional
    public void sendToAllDevice(MessageForAllDto messageDto) {
        List<FCMDevice>  fcmDevices = fcmDeviceRepository.findAllByRequestLocationAndNotificationSettings_AvailableNotificationContains(messageDto.getRequestLocation(), messageDto.getNotificationType());

        for(FCMDevice fcmDevice : fcmDevices){
            Receiver receiver = new Receiver(fcmDevice.getDevice(), fcmDevice.getDeviceType());
            Message message = messageDto.toEntity(receiver);

            messageBox.collectMessage(message);
        }
    }

    @Override
    @Transactional
    public void sendToDevice(MessageForAccountDto messageDto) {
        Account account = accountRepository.findByEmail(messageDto.getEmail());
        NotificationSettings notification = notificationRepository.findByAccount(account);

        if(!notification.isNotification(messageDto.getNotificationType())) return;

        List<FCMDevice>  fcmDevices = fcmDeviceRepository.findAllByAccount(account);

        for(FCMDevice fcmDevice : fcmDevices){
            System.out.println(fcmDevice.getNotificationSettings());
            Receiver receiver = new Receiver(fcmDevice.getDevice(), fcmDevice.getDeviceType());
            Message message = messageDto.toEntity(receiver);

            messageBox.collectMessage(message);
        }
    }
}
