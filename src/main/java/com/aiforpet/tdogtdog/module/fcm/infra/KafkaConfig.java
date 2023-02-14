package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value(value = "${spring.kafka.fcm.bootstrap-servers}")
    private String bootstrapAddress;

    @Value(value = "${spring.kafka.fcm.producer.batch-size}")
    private int PRODUCER_BATCH_SIZE;
    @Value(value = "${spring.kafka.fcm.producer.linger-ms}")
    private int LINGER_MS;

    @Value(value = "${spring.kafka.fcm.consumer.delay}")
    private int CONSUME_DELAY_MS;
    @Value(value = "${spring.kafka.fcm.consumer.batch-size}")
    private int CONSUMER_BATCH_SIZE;
    @Value(value = "${spring.kafka.fcm.consumer.number}")
    private int CONSUMER_NUMBER;

    @Value(value = "${spring.kafka.fcm.group-id}")
    private String GROUP_ID;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        return new KafkaAdmin(configs);
    }


    @Bean
    public NewTopic topic1() {
        return new NewTopic("fcm", 1, (short) 1);
    }

    @Bean
    public ProducerFactory<String, Message> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                CustomSerializer.class);
        configProps.put(
                ProducerConfig.BATCH_SIZE_CONFIG,
                PRODUCER_BATCH_SIZE
        );
        configProps.put(
          ProducerConfig.LINGER_MS_CONFIG,
          LINGER_MS
        );
        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, Message> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    @Bean
    public ConsumerFactory<? super String, ? super Message> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(
                ConsumerConfig.GROUP_ID_CONFIG,
                GROUP_ID);
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                CustomDeserializer.class);
        props.put(
                ConsumerConfig.MAX_POLL_RECORDS_CONFIG,
                CONSUMER_BATCH_SIZE);
        props.put(
                ConsumerConfig.AUTO_OFFSET_RESET_CONFIG,
                "latest");
        props.put(
                ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG,
                true);
        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Message>
    kafkaListenerContainerFactory() {

        ConcurrentKafkaListenerContainerFactory<String, Message> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        factory.getContainerProperties().setIdleBetweenPolls(CONSUME_DELAY_MS);
        factory.setBatchListener(true);
        factory.setConcurrency(CONSUMER_NUMBER);
        return factory;
    }
}
