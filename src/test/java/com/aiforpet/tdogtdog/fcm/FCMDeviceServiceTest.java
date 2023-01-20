package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.AccountHelper;
import com.aiforpet.tdogtdog.fcm.helper.TestAccountRepository;
import com.aiforpet.tdogtdog.fcm.helper.TestFCMDeviceRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.DeviceType;
import com.aiforpet.tdogtdog.module.fcm.service.FCMDeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class FCMDeviceServiceTest {

    private final FCMDeviceService fcmDeviceService;
    private final AccountHelper accountHelper;
    private final TestAccountRepository testAccountRepository;
    private final String email = "test";

    @Autowired
    public FCMDeviceServiceTest(FCMDeviceService fcmDeviceService, AccountHelper accountHelper, TestAccountRepository testAccountRepository) {
        this.fcmDeviceService = fcmDeviceService;
        this.accountHelper = accountHelper;
        this.testAccountRepository = testAccountRepository;
    }

    @BeforeEach
    public void deleteAll(@Autowired TestFCMDeviceRepository testFCMDeviceRepository){
        testFCMDeviceRepository.deleteAllInBatch();
    }

    @Nested
    class CreateDeviceTest{
        @Test
        public void testCreateDevice(){
            accountHelper.createAccount(email);
            fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), "123", DeviceType.IOS);

            assertEquals(testAccountRepository.findAllByEmail(email).size(), 1);
        }

        @Test
        public void testCreateDuplicateDevice(){

        }


    }



}
