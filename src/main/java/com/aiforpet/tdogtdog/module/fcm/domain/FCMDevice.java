package com.aiforpet.tdogtdog.module.fcm.domain;


import com.aiforpet.tdogtdog.module.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
public class FCMDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(unique = true, nullable = false)
    private String device;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Column(nullable = false)
    private Instant time;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RequestLocation requestLocation;

    public FCMDevice(Account account, String device, DeviceType deviceType, RequestLocation requestLocation){
        this.account = account;
        this.device = device;
        this.deviceType = deviceType;
        this.time = Instant.now();
        this.requestLocation = requestLocation;
    }

    public void updateTime(){
        this.time = Instant.now();
    }
    public void updateDevice(String device) {
        this.device = device;
        this.time = Instant.now();
    }

    public void updateRequestLocation(RequestLocation requestLocation){
        this.requestLocation = requestLocation;
    }
}
