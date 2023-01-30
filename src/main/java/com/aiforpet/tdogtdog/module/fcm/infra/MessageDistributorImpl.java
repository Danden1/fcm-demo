package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.DestroyChecker;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.ResendChecker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Component
public class MessageDistributorImpl implements MessageDistributor {

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


    @Scheduled(fixedDelay = 100)
    @Transactional
    public void takeOutMessages(){
        List<MessageEntity> messageEntities = messageBoxRepository.findTop8ByOrderByIdAsc();
        List<Message> messages = new ArrayList<>();

        for(MessageEntity messageEntity : messageEntities) {
            Message message = messageEntityMapper.mapMessageEntitytoMessage(messageEntity);
            messageBoxRepository.delete(messageEntity);

            if(isDestroy(message)){
                continue;
            }
            if(isResend(message)){
                messageBoxRepository.save(messageEntityMapper.mapMessageToMessageEntity(message));
                continue;
            }

            messages.add(message);
        }

        distributeMessages(messages);
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
