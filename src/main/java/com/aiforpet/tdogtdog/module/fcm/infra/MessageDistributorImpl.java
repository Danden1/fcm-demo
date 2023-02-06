package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.DestroyChecker;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.ResendChecker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MessageDistributorImpl implements MessageDistributor {

    private final MessageBox kafkaMessageBox;

    private final List<DestroyChecker> destroyCheckers;
    private final List<ResendChecker> resendCheckers;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());;

    private final Messenger messenger;

    public MessageDistributorImpl(MessageBox kafkaMessageBox, @Autowired List<DestroyChecker> destroyCheckers, @Autowired List<ResendChecker> resendCheckers, Messenger messenger){
        this.kafkaMessageBox = kafkaMessageBox;
        this.destroyCheckers = destroyCheckers;
        this.resendCheckers = resendCheckers;
        this.messenger = messenger;
    }


    @Override
    @Transactional
    @KafkaListener(topics = "${spring.kafka.fcm.topic}", groupId = "${spring.kafka.fcm.group-id}")
    public void distributeMessages(List<Message> messages){
        log.info(String.format("Take Out %d messages.", messages.size()));
        int messageCount = messages.size();
        Thread[] threads = new Thread[messageCount];

        for(int i = 0; i < messages.size(); i++){
            Message message = messages.get(i);
            threads[i] = new Thread(() -> {

                if(isValidMessage(message)) {
                    messenger.deliverMessage(message);
                }
            });

            threads[i].start();
        }

        for(int i = 0; i< messageCount; i++){
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private boolean isValidMessage(Message message){
        if(isDestroy(message)){
            log.info(String.format("Destroy Message [title : %s, body : %s, data : %s, device : %s]", message.getTitle(), message.getBody(), message.getData(), message.getReceiveDevice()));;
            return false;
        }
        if(isResend(message)){
            log.info(String.format("Resend Message [title : %s, body : %s, data : %s, device : %s]", message.getTitle(), message.getBody(), message.getData(), message.getReceiveDevice()));;
            kafkaMessageBox.collectMessage(message);
            return false;
        }
        return true;
    }


    private boolean isDestroy(Message message){
        for(DestroyChecker destroyChecker : destroyCheckers){
            if(destroyChecker.isDestroy(message)) return true;
        }

        return false;
    }

    private boolean isResend(Message message){
        for(ResendChecker resendChecker : resendCheckers){
            if(resendChecker.isResend(message)) return true;
        }
        return false;
    }
}
