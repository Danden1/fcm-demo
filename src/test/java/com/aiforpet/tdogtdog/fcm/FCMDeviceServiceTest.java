package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.AccountHelper;
import com.aiforpet.tdogtdog.fcm.helper.TestAccountRepository;
import com.aiforpet.tdogtdog.fcm.helper.TestFCMDeviceRepository;
import com.aiforpet.tdogtdog.fcm.helper.TestNotificationRepository;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.service.FCMDeviceService;
import org.junit.jupiter.api.*;
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
    private final TestNotificationRepository testNotificationRepository;
    private final String email = "test";

    @Autowired
    public FCMDeviceServiceTest(FCMDeviceService fcmDeviceService, AccountHelper accountHelper, TestAccountRepository testAccountRepository, TestFCMDeviceRepository testFCMDeviceRepository, TestNotificationRepository testNotificationRepository) {
        this.fcmDeviceService = fcmDeviceService;
        this.accountHelper = accountHelper;
        this.testAccountRepository = testAccountRepository;
        this.testFCMDeviceRepository = testFCMDeviceRepository;
        this.testNotificationRepository = testNotificationRepository;
    }

    @AfterEach
    public void deleteAll(){
        testAccountRepository.deleteAllInBatch();
    }

    @Nested
    class CreateDeviceTest{
        private final String initToken = "123";
        @Test
        @DisplayName("디바이스 생성 테스트")
        public void testCreateDevice(){
            accountHelper.createAccount(email);
            fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), initToken, DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);

            assertEquals(1, testAccountRepository.findAllByEmail(email).size());
        }

        @Test
        @DisplayName("같은 디바이스 생성 될 경우 에러 발생")
        public void testCreateDuplicateDevice(){
            accountHelper.createAccount(email);
            fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), initToken, DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);

            assertThrows(Exception.class, ()->fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), initToken, DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME));
        }
    }

    @Nested
    class UpdateTokenTest{

        private final String afterToken = "918239";
        private final String initToken = "123";
        @Test
        @DisplayName("새로운 토큰으로 업데이트. 시간 및 토큰 값 테스트")
        public void testUpdateToken(){
            Instant beforeTime = Instant.now();

            accountHelper.createAccount(email);
            Account account = testAccountRepository.findByEmail(email);
            fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), initToken, DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);
            fcmDeviceService.updateToken(testFCMDeviceRepository.findByAccount(account).getDevice(), afterToken);

            assertEquals(afterToken, testFCMDeviceRepository.findByDevice(afterToken).getDevice());
            assertFalse(testFCMDeviceRepository.findByDevice(afterToken).getTime().isBefore(beforeTime));
        }

        @Test
        @DisplayName("이미 존재하는 디바이스로 업데이트 할 경우 에러 발생")
        public void testUpdateDuplicatedToken(){
            accountHelper.createAccount(email);
            fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), initToken, DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);
            fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), afterToken, DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);

            assertThrows(Exception.class, ()->fcmDeviceService.updateToken(testFCMDeviceRepository.findByDevice(initToken).getDevice(), afterToken));
        }
    }

    @Nested
    class TimeUpdateTest{
        private final Instant testTime = Instant.now().minus(1, ChronoUnit.HOURS);
        private final String token = "123";
        @Test
        @DisplayName("현재 시간으로 업데이트 되는지 테스트")
        public void testTimeUpdate(){
            accountHelper.createAccount(email);
            Account account = testAccountRepository.findByEmail(email);

            fcmDeviceService.createDevice(testAccountRepository.findByEmail(email), token, DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);

            FCMDevice fcmDevice = testFCMDeviceRepository.findByDevice(token);
            ReflectionTestUtils.setField(fcmDevice, "time", testTime);

            fcmDeviceService.updateTime(token);

            assertTrue(testTime.isBefore(testFCMDeviceRepository.findByDevice(token).getTime().minus(1,ChronoUnit.HOURS)));
        }
    }

    @Nested
    class UpdateNotificationTest{
        private final String token = "123";
        @Test
        @DisplayName("important 알림 꺼지는지 테스트(default는 ON)")
        public void testUpdateImportantNotification(){
            accountHelper.createAccount(email);
            Account account = testAccountRepository.findByEmail(email);

            fcmDeviceService.updateAccountNotification(account, NotificationType.IMPORTANT, NotificationSetting.OFF);

            assertFalse(testNotificationRepository.findByAccount(account).getImportant());
        }
    }



}
