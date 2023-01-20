package com.aiforpet.tdogtdog.module.fcm.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class HttpV1MessageDto {
    private NotificationDto notification;
    private String token;
    private Map<String, Object> data;
}
