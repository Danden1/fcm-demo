package com.aiforpet.tdogtdog.error;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalErrorController {

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String,Object>> handleNullPointerException(NullPointerException ex){
        Map<String, Object> res = new HashMap<>();

        res.put("message", ex.getMessage());
        res.put("status", "error");
        res.put("data", null);

        return new ResponseEntity<Map<String,Object>>(res, HttpStatus.UNPROCESSABLE_ENTITY);
    }

}
