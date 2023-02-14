package com.aiforpet.tdogtdog.controller;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.account.AccountRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.DeviceType;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationControl;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationType;
import com.aiforpet.tdogtdog.module.fcm.domain.RequestLocation;
import com.aiforpet.tdogtdog.module.fcm.service.FCMDeviceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/devices")
public class FCMDeviceController {


    private final FCMDeviceService fcmDeviceService;
    private final AccountRepository accountRepository;

    public FCMDeviceController(FCMDeviceService fcmDeviceService, AccountRepository accountRepository) {
        this.fcmDeviceService = fcmDeviceService;
        this.accountRepository = accountRepository;
    }

    @PostMapping
    public ResponseEntity<String> registerDevice(@RequestBody Map<String, String> req){
        Account account = accountRepository.findByEmail(req.get("email"));
        fcmDeviceService.createDevice(account, req.get("device"), DeviceType.from(req.get("deviceType")), RequestLocation.from(req.get("requestLocation")));

        return ResponseEntity.ok(null);
    }

    @PutMapping("/notification/settings")
    public ResponseEntity<String> updateNotificationSettings(@RequestBody Map<String, String> req){

        fcmDeviceService.updateNotificationSettings(accountRepository.findByEmail(req.get("email")), NotificationType.from(req.get("notificationType")), NotificationControl.from(req.get("notificationControl")));

        return ResponseEntity.ok(null);
    }

    @PutMapping("/location")
    public ResponseEntity<String> updateRequestLocation(@RequestBody Map<String, String> req){

        fcmDeviceService.updateRequestLocation(req.get("device"), RequestLocation.from(req.get("requestLocation")));

        return ResponseEntity.ok(null);
    }

    @PutMapping("/token")
    public ResponseEntity<String> updateToken(@RequestBody Map<String, String> req){

        fcmDeviceService.updateToken(req.get("beforeDevice"), req.get("afterDevice"));

        return ResponseEntity.ok(null);
    }

    @PutMapping("/time")
    public ResponseEntity<String> updateTime(@RequestBody Map<String, String> req){

        fcmDeviceService.updateTime(req.get("device"));

        return ResponseEntity.ok(null);
    }

}
