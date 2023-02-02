package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.DestroyChecker;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.ResendChecker;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageDistributorImpl implements MessageDistributor {
    private final String TOPIC = "fcm";

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


    @KafkaListener(topics = TOPIC, groupId = "fcm")
    public void takeOutMessages(List<String> messages){
        List<Message> validMessages = new ArrayList<>();
        System.out.println("listen");
        System.out.println(messages.size());
        for(String messageEntity : messages) {
            Message message = null;
            try {
                message = objectMapper.readValue(messageEntity, Message.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            if(isDestroy(message)){
                continue;
            }
            if(isResend(message)){
                System.out.println("?!");
                kafkaMessageBox.collectMessage(message);
                continue;
            }

            validMessages.add(message);
        }

        distributeMessages(validMessages);
    }

    @Override
    public void distributeMessages(List<Message> messages) {
        int messageCount = messages.size();
        Thread[] threads = new Thread[messageCount];

        for(int i = 0; i < messages.size(); i++){
            int threadI = i;
            threads[i] = new Thread(() -> messenger.deliverMessage(messages.get(threadI)));
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
