package com.aiforpet.tdogtdog.module.fcm.dto;


import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.InvalidMessageException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class MessageForAccountDto {
    private String title;
    private String body;
    private Map<String, Object> data;
    private LocalDateTime reservationTime;
    private LocalDateTime timeLimit;
    private RequestLocation requestLocation;
    private NotificationType notificationType;

    private String email;



    public Message toEntity(Receiver receiver) throws InvalidMessageException {
        if(timeLimit == null){
            timeLimit = LocalDateTime.now().plusDays(1);
        }
        if(reservationTime == null){
            reservationTime = LocalDateTime.now();
        }

        MessageConstraint messageConstraint = new MessageConstraint(notificationType,timeLimit,requestLocation,reservationTime);

        return new Message(title, body, data, receiver, messageConstraint);
    }
}
