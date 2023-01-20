package com.aiforpet.tdogtdog.module.fcm.domain;


import com.aiforpet.tdogtdog.module.account.Account;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.Instant;

@Entity
@Getter
@NoArgsConstructor
public class FCMDevice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name = "account_id")
    private Account account;

    @Column(unique = true)
    private String device;

    @Column
    @Enumerated(EnumType.STRING)
    private DeviceType deviceType;

    @Column
    private Instant time;

    public FCMDevice(Account account, String device, DeviceType deviceType){
        this.account = account;
        this.device = device;
        this.deviceType = deviceType;
        this.time = Instant.now();
    }

    public void updateTime(){
        this.time = Instant.now();
    }
    public void updateDevice(String device) {
        this.device = device;
        this.time = Instant.now();
    }
}
