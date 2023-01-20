package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.NotificationRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.validator.MessageNotificationValidator;
import com.aiforpet.tdogtdog.module.fcm.domain.validator.MessagePushTimeValidator;
import com.aiforpet.tdogtdog.module.fcm.domain.validator.MessageTimeLimitValidator;
import com.aiforpet.tdogtdog.module.fcm.domain.validator.MessageValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ValidatorConfiguration {

    @Bean
    @Order(3)
    public MessageValidator messagePushTimeValidator(){
        return new MessagePushTimeValidator();
    }

    @Bean
    @Order(1)
    public MessageValidator messageTimeLimitValidator(){
        return new MessageTimeLimitValidator();
    }

    @Bean
    @Autowired
    @Order(2)
    public MessageValidator messageNotificationValidator(NotificationRepository notificationRepository){
        return new MessageNotificationValidator(notificationRepository);
    }
}
