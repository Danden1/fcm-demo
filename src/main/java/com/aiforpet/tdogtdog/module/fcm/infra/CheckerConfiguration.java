package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.FCMDeviceRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CheckerConfiguration {

    @Bean
    public ResendChecker messageSendingTimeChecker(){
        return new SendingTimeChecker();
    }

    @Bean
    public ResendChecker messageReservationChecker(){
        return new ReservationChecker();
    }

    @Bean
    public DestroyChecker timeLimitChecker(){
        return new TimeLimitChecker();

    }

    @Bean
    @Autowired
    public DestroyChecker notificationChecker(FCMDeviceRepository fcmDeviceRepository){
        return new NotificationChecker(fcmDeviceRepository);
    }
}
