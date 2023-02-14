package com.aiforpet.tdogtdog.controller;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.account.AccountRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationSettings;
import com.aiforpet.tdogtdog.module.fcm.domain.NotificationSettingsRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;
import java.util.Map;

@RestController
public class AccountController {

    private final AccountRepository accountRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;

    public AccountController(AccountRepository accountRepository, NotificationSettingsRepository notificationSettingsRepository) {
        this.accountRepository = accountRepository;
        this.notificationSettingsRepository = notificationSettingsRepository;
    }

    @PostMapping("/account")
    @Transactional
    public ResponseEntity<String> createAccount(@RequestBody Map<String, String> req){
        Account account = new Account();
        account.setEmail(req.get("email"));

        accountRepository.save(account);

        NotificationSettings notificationSettings = new NotificationSettings(account);


        notificationSettingsRepository.save(notificationSettings);


        return ResponseEntity.ok(null);
    }

}
