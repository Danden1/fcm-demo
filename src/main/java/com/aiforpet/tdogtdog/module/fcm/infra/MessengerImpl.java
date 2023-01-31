package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.Messenger;
import com.aiforpet.tdogtdog.module.fcm.dto.PushMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class MessengerImpl implements Messenger {

    private final HttpMessageMapper httpMessageMapper;
    private final ObjectMapper objectMapper;

    private final String firebaseUrl = "https://fcm.googleapis.com/fcm/send";
    private final String firebaseAuthKey = "";

    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders httpHeaders = new HttpHeaders();

    public MessengerImpl(HttpMessageMapper httpMessageMapper) {
        this.httpMessageMapper = httpMessageMapper;
        this.objectMapper = new ObjectMapper();
        this.httpHeaders.add("Content-Type", "application/json; charset=utf-8");
        this.httpHeaders.add("Authorization", this.firebaseAuthKey);
    }

    @Override
    public void deliverMessage(Message message) {
        PushMessageDto httpMessageDto = httpMessageMapper.mapMessage(message);
        HttpEntity<PushMessageDto> entity = new HttpEntity<>(httpMessageDto, httpHeaders);
        restTemplate.postForObject(this.firebaseUrl, entity, String.class);

        try {
            System.out.println(objectMapper.writeValueAsString(httpMessageDto));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
