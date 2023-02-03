package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.MessageBox;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;


@Component
@EnableAsync
@Slf4j
public class KafkaMessageBox implements MessageBox {

    private final KafkaTemplate<String, Message> kafkaTemplate;

    @Value("${spring.kafka.fcm.topic}")
    private String TOPIC;

    public KafkaMessageBox(KafkaTemplate<String, Message> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Async
    public void collectMessage(Message message) {
        kafkaTemplate.send(TOPIC, message);
        log.info(String.format("Collect Message [title : %s, body : %s, data : %s, device : %s]", message.getTitle(), message.getBody(), message.getData(), message.getReceiveDevice()));

    }

}
