package com.aiforpet.tdogtdog.module.fcm.domain.checker;

import com.aiforpet.tdogtdog.module.fcm.domain.FCMDeviceRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.Notification;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationType;

public class NotificationChecker implements DestroyChecker {

    private final FCMDeviceRepository fcmDeviceRepository;

    public NotificationChecker(FCMDeviceRepository fcmDeviceRepository) {
        this.fcmDeviceRepository = fcmDeviceRepository;
    }

    @Override
    public boolean isDestroy(Message message) {

        Notification deviceNotification = fcmDeviceRepository.findNotificationByDevice(message.getReceiveDevice());
        NotificationType messageNotificationType = message.getNotificationType();

        return !deviceNotification.isNotification(messageNotificationType);
    }


}
