package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.DeviceType;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationType;
import com.aiforpet.tdogtdog.module.fcm.domain.RequestLocation;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String body;

    @Column
    private String data;

    @Column(nullable = false)
    private String receiveDevice;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private NotificationType notificationType;

    @Column(nullable = false)
    private LocalDateTime timeLimit;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestLocation requestLocation;


    public void copy(MessageEntity messageEntity){
        this.data = messageEntity.getData();
        this.title = messageEntity.getTitle();
        this.body = messageEntity.getBody();
        this.deviceType = messageEntity.getDeviceType();
        this.requestLocation = messageEntity.getRequestLocation();
        this.receiveDevice = messageEntity.getReceiveDevice();
        this.notificationType = messageEntity.getNotificationType();
        this.timeLimit = messageEntity.getTimeLimit();
    }
}
