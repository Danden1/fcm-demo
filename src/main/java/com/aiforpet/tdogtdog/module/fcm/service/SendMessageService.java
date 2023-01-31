package com.aiforpet.tdogtdog.module.fcm.service;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationType;
import com.aiforpet.tdogtdog.module.fcm.domain.RequestLocation;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;

public interface SendMessageService {
    void sendToAllDevice(NotificationType notificationType, String body, String title, Map<String, Object> data, RequestLocation requestLocation, LocalDateTime timeLimit);
    void sendToDevice(Account account, NotificationType notificationType, String body, String title, Map<String, Object> data, RequestLocation requestLocation, LocalDateTime timeLimit);

}
