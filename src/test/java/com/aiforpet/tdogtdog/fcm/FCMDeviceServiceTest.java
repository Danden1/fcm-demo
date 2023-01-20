package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.AccountHelper;
import com.aiforpet.tdogtdog.fcm.helper.TestAccountRepository;
import com.aiforpet.tdogtdog.fcm.helper.TestFCMDeviceRepository;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.DeviceType;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDevice;
import com.aiforpet.tdogtdog.module.fcm.service.FCMDeviceService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class FCMDeviceServiceTest {

    private final FCMDeviceService fcmDeviceService;

    private final AccountHelper accountHelper;
    private final TestAccountRepository testAccountRepository;
    private final TestFCMDeviceRepository testFCMDeviceRepository;
    private final String email = "test";

    @Autowired
    public FCMDeviceServiceTest(FCMDeviceService fcmDeviceService, AccountHelper accountHelper, TestAccountRepository testAccountRepository, TestFCMDeviceRepository testFCMDeviceRepository) {
        this.fcmDeviceService = fcmDeviceService;
        this.accountHelper = accountHelper;
        this.testAccountRepository = testAccountRepository;
        this.testFCMDeviceRepository = testFCMDeviceRepository;
    }

    @BeforeEach
    public void deleteAll(){
        testFCMDeviceRepository.deleteAllInBatch();
    }

    @Nested
    class CreateDeviceTest{
        private final String initToken = "123";
        @Test
        public void testCreateDevice(){
            accountHelper.createAccount(email);
            fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), initToken, DeviceType.IOS);

            assertEquals(1, testAccountRepository.findAllByEmail(email).size());
        }

        @Test
        public void testCreateDuplicateDevice(){
            accountHelper.createAccount(email);
            fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), initToken, DeviceType.IOS);

            assertThrows(Exception.class, ()->fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), initToken, DeviceType.IOS));
        }
    }

    @Nested
    class UpdateTokenTest{

        private final String afterToken = "918239";
        private final String initToken = "123";
        @Test
        public void testUpdateToken(){
            accountHelper.createAccount(email);
            Account account = testAccountRepository.findByEmail(email);
            fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), initToken, DeviceType.IOS);


            assertEquals(afterToken, fcmDeviceService.updateToken(testFCMDeviceRepository.findByAccount(account).getDevice(), afterToken));
        }

        @Test
        public void testUpdateDuplicatedToken(){
            accountHelper.createAccount(email);
            Account account = testAccountRepository.findByEmail(email);
            fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), initToken, DeviceType.IOS);
            fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), afterToken, DeviceType.IOS);

            assertThrows(Exception.class, ()->fcmDeviceService.updateToken(testFCMDeviceRepository.findByDevice(initToken).getDevice(), afterToken));
        }
    }

    @Nested
    class TimeUpdateTest{
        private final Instant testTime = Instant.now().minus(1, ChronoUnit.HOURS);
        private final String token = "123";
        @Test
        public void testTimeUpdate(){
            accountHelper.createAccount(email);
            Account account = testAccountRepository.findByEmail(email);

            fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), token, DeviceType.IOS);

            FCMDevice fcmDevice = testFCMDeviceRepository.findByDevice(token);
            ReflectionTestUtils.setField(fcmDevice, "time", testTime);

            fcmDeviceService.updateTime(token);


            assertTrue();
        }
    }



}
