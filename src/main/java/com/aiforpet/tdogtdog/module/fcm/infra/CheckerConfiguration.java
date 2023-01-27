package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.FCMDeviceRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CheckerConfiguration {

    @Bean
    public ResendChecker messagePushTimeValidator(){
        return new SendingTimeChecker();
    }

    @Bean
    public DestroyChecker timeLimitChekcer(){
        return new TimeLimitChecker();

    }

    @Bean
    @Autowired
    public DestroyChecker notificationChecker(FCMDeviceRepository fcmDeviceRepository){
        return new NotificationChecker(fcmDeviceRepository);
    }
}
