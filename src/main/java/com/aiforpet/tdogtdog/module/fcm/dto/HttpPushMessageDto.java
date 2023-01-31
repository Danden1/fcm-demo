package com.aiforpet.tdogtdog.module.fcm.dto;


import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class HttpPushMessageDto implements PushMessageDto{
    private String to;
    private NotificationDto notification;
    private Map<String, Object> data;

}
