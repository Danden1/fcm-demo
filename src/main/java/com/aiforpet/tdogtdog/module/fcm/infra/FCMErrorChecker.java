package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.exception.FCMErrorType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;



public class FCMErrorChecker {

    public boolean isError(Map<String, Object> response){
        if(getResponseResult(response).containsKey("error")){
            return true;
        }
        return false;
    }

    public FCMErrorType getErrorType(int httpCode){
        if(httpCode == 500){
            return FCMErrorType.INTERNAL_SERVER_ERROR;
        }
        if(httpCode > 500){
            return FCMErrorType.TIMEOUT;
        }
        if(httpCode == 400){
            return FCMErrorType.INVALID_JSON;
        }
        throw new IllegalArgumentException("Can't find Error in httpCode");
    }

    public FCMErrorType getErrorType(Map<String, Object> response){
        Map<String, String> result = getResponseResult(response);
        String error = result.get("error");

        if(error.equals("InvalidRegistration")){
            return FCMErrorType.INVALID_TOKEN;
        }
        if(error.equals("NotRegistered")){
            return FCMErrorType.UNREGISTERED;
        }
        if(error.equals("MessageTooBig")){
            return FCMErrorType.MESSAGE_TOO_BIG;
        }
        if(error.equals("InternalServerError")){
            return FCMErrorType.INTERNAL_SERVER_ERROR;
        }
        if(error.equals("DeviceMessageRateExceeded")){
            return FCMErrorType.DEVICE_MESSAGE_RATE_EXCEEDED;
        }
        if(error.equals("Unavailable")){
            return FCMErrorType.TIMEOUT;
        }
        throw new IllegalArgumentException("Can't find Error in response");
    }

    private Map<String,String> getResponseResult(Map<String, Object> response){
        List<Map<String,String>> results = (List<Map<String, String>>) response.get("results");

        return results.get(0);
    }
}
