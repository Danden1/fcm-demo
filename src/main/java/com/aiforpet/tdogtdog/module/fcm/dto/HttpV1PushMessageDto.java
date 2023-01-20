package com.aiforpet.tdogtdog.module.fcm.dto;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class HttpV1PushMessageDto implements PushMessageDto {
    HttpV1MessageDto message;
}
