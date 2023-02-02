package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.Messenger;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.FCMErrorHandler;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.FCMErrorType;
import com.aiforpet.tdogtdog.module.fcm.dto.PushMessageDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Slf4j
public class MessengerImpl implements Messenger {

    private final HttpMessageMapper httpMessageMapper;
    private final String firebaseUrl = "https://fcm.googleapis.com/fcm/send";
    private final String firebaseAuthKey = "";

    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders httpHeaders = new HttpHeaders();

    private final FCMErrorChecker fcmErrorChecker = new FCMErrorChecker();
    private final FCMErrorHandler fcmErrorHandler;

    public MessengerImpl(HttpMessageMapper httpMessageMapper, FCMErrorHandler fcmErrorHandler) {
        this.httpMessageMapper = httpMessageMapper;
        this.fcmErrorHandler = fcmErrorHandler;
        this.httpHeaders.add("Content-Type", "application/json; charset=utf-8");
        this.httpHeaders.add("Authorization", this.firebaseAuthKey);
    }

    @Override
    public void deliverMessage(Message message) {
        PushMessageDto httpMessageDto = httpMessageMapper.mapMessage(message);
        HttpEntity<PushMessageDto> entity = new HttpEntity<>(httpMessageDto, httpHeaders);

        try {
            Map<String, Object> res = restTemplate.postForObject(this.firebaseUrl, entity, Map.class);

            if (fcmErrorChecker.isError(res)) {
                FCMErrorType fcmErrorType = fcmErrorChecker.getErrorType(res);
                fcmErrorHandler.handleError(fcmErrorType, message);
            }
            else {
                log.info(String.format("Push Success [title : %s, body : %s, data : %s, device : %s]", message.getTitle(), message.getBody(), message.getData(), message.getReceiveDevice()));
            }
        }catch(HttpClientErrorException e) {
            int httpCode = e.getRawStatusCode();

            FCMErrorType fcmErrorType = fcmErrorChecker.getErrorType(httpCode);
            fcmErrorHandler.handleError(fcmErrorType, message);
        }
    }
}
