package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.MessageException;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.PushTimeException;
import com.aiforpet.tdogtdog.module.fcm.domain.validator.MessageValidator;
import com.aiforpet.tdogtdog.module.fcm.dto.PushMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

@Component
public class PusherImpl implements Pusher {

    private final MessageBoxRepository messageBoxRepository;
    private final MessageEntityMapper messageEntityMapper;
    private final HttpV1MessageMapper httpV1MessageMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final List<MessageValidator> messageValidators;

    public PusherImpl(MessageBoxRepository messageBoxRepository, MessageEntityMapper messageEntityMapper, HttpV1MessageMapper httpV1PushMessageMapper, List<MessageValidator> messageValidators){
        this.messageBoxRepository = messageBoxRepository;
        this.messageEntityMapper = messageEntityMapper;
        this.httpV1MessageMapper = httpV1PushMessageMapper;
        this.messageValidators = messageValidators;
    }
    @Override
    public void push(Message message) {
        PushMessageDto httpV1MessageDto = httpV1MessageMapper.mapMessage(message);

        try {
            System.out.println(objectMapper.writeValueAsString(httpV1MessageDto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Scheduled(fixedDelay = 100)
    @Transactional
    public void takeOfMessage(){
        List<MessageEntity> messageEntities = messageBoxRepository.findTop8ByOrderByIdAsc();


        Thread[] threads = new Thread[messageEntities.size()];
        int i = 0;

        for(MessageEntity messageEntity : messageEntities) {
            Message message = messageEntityMapper.mapMessageEntitytoMessage(messageEntity);
            messageBoxRepository.delete(messageEntity);

            try {
                assertValidMessage(message);
            }
            catch(PushTimeException e){
                MessageEntity copyMessageEntity =new MessageEntity();
                copyMessageEntity.copy(messageEntity);

                messageBoxRepository.save(copyMessageEntity);
                continue;
            }
            catch(MessageException e){
                continue;
            }

            threads[i] = new Thread(new Runnable() {
                @Override
                public void run() {
                    push(message);
                }
            });
            threads[i].start();

            i += 1;
        }

        for(int j = 0; j<i; j++){
            try {
                threads[j].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void assertValidMessage(Message message) throws MessageException {
        for(MessageValidator messageValidator : messageValidators){
            messageValidator.assertValidMessage(message);
        }
    }
}
