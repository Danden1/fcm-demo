package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.DestroyChecker;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.ResendChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class MessageDistributorImpl implements MessageDistributor{

    private final MessageBoxRepository messageBoxRepository;
    private final MessageEntityMapper messageEntityMapper;

    private final List<DestroyChecker> destroyCheckers;
    private final List<ResendChecker> resendCheckers;

    private final Messenger messenger;

    public MessageDistributorImpl(MessageBoxRepository messageBoxRepository, MessageEntityMapper messageEntityMapper, @Autowired List<DestroyChecker> destroyCheckers, @Autowired List<ResendChecker> resendCheckers, Messenger messenger){
        this.messageBoxRepository = messageBoxRepository;
        this.messageEntityMapper = messageEntityMapper;
        this.destroyCheckers = destroyCheckers;
        this.resendCheckers = resendCheckers;
        this.messenger = messenger;
    }

    @EventListener
    public void takeMessages(MessageEvent messageEvent){
        takeOutMessages(messageEvent.getMessages());
    }


    @Transactional
    public void takeOutMessages(List<Message> messages){
        List<Message> validMessages = new ArrayList<>();
        log.info(String.format("Take Out %d messages.", messages.size()));

        for(Message message : messages) {;

            if(isDestroy(message)){
                log.info(String.format("Destroy Message [title : %s, body : %s, data : %s, device : %s]", message.getTitle(), message.getBody(), message.getData(), message.getReceiveDevice()));;
                continue;
            }
            if(isResend(message)){
                log.info(String.format("Resend Message [title : %s, body : %s, data : %s, device : %s]", message.getTitle(), message.getBody(), message.getData(), message.getReceiveDevice()));;
                messageBoxRepository.save(messageEntityMapper.mapMessageToMessageEntity(message));
                continue;
            }

            log.info(String.format("Valid Message [title : %s, body : %s, data : %s, device : %s]", message.getTitle(), message.getBody(), message.getData(), message.getReceiveDevice()));;

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
