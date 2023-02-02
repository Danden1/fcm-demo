package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.MessageBox;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;


@Component
@EnableAsync
public class KafkaMessageBox implements MessageBox {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final String TOPIC = "fcm";

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());;

    public KafkaMessageBox(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    @Async
    public void collectMessage(Message message) {

        try {
            String produceMessage = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(TOPIC, produceMessage);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
