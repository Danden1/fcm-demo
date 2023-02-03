package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.MessageBox;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;


@Component
@EnableAsync
@Slf4j
public class DBMessageBox implements MessageBox {


    private final MessageBoxRepository messageBoxRepository;
    private final MessageEntityMapper messageEntityMapper;

    private final ApplicationEventPublisher applicationEventPublisher;

    public DBMessageBox(MessageBoxRepository messageBoxRepository, MessageEntityMapper messageEntityMapper, ApplicationEventPublisher applicationEventPublisher){
        this.messageBoxRepository = messageBoxRepository;
        this.messageEntityMapper = messageEntityMapper;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    @Transactional
    @Async
    public void collectMessage(Message message) {

        MessageEntity messageEntity = messageEntityMapper.mapMessageToMessageEntity(message);
        log.info(String.format("Collect Message [title : %s, body : %s, data : %s, device : %s]", message.getTitle(), message.getBody(), message.getData(), message.getReceiveDevice()));
        messageBoxRepository.save(messageEntity);
    }

    @Scheduled(fixedDelay = 100)
    @Transactional
    public void eventMaker(){
        List<MessageEntity> messageEntities = messageBoxRepository.findTop8ByOrderByIdAsc();
        messageBoxRepository.deleteAllInBatch(messageEntities);


        //stream의 parallel 은 안 쓰도록. collect 부분에서 성능 저하가 일어남. 합치느 데 부담이 큼.
        List<Message> messages = messageEntities.stream().map(messageEntityMapper::mapMessageEntitytoMessage).collect(Collectors.toList());
        applicationEventPublisher.publishEvent(new MessageEvent(messages));

    }

}
