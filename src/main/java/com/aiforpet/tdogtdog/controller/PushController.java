package com.aiforpet.tdogtdog.controller;

import com.aiforpet.tdogtdog.module.fcm.dto.MessageForAccountDto;
import com.aiforpet.tdogtdog.module.fcm.dto.MessageForAllDto;
import com.aiforpet.tdogtdog.module.fcm.service.SendMessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/push")
public class PushController {
    private final SendMessageService sendMessageService;


    public PushController(SendMessageService sendMessageService) {
        this.sendMessageService = sendMessageService;
    }

    @GetMapping("/all")
    public ResponseEntity<String> sendToAll(@RequestBody MessageForAllDto messageForAllDto){
        sendMessageService.sendToAllDevice(messageForAllDto);

        return ResponseEntity.ok(null);
    }

    @GetMapping("/device")
    public ResponseEntity<String> sendToAll(@RequestBody MessageForAccountDto messageForAccountDto){
        sendMessageService.sendToDevice(messageForAccountDto);

        return ResponseEntity.ok(null);
    }

}
