package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.*;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.service.SendMessageService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
public class SendMessageServiceTest {
    private final SendMessageService sendMessageService;
    private final DBMessageBoxRepoHelper dbMessageBoxRepoHelper;
    private final FCMDeviceHelper fcmDeviceHelper;
    private final AccountHelper accountHelper;
    private final TestNotificationRepository testNotificationRepository;
    private final TestAccountRepository testAccountRepository;

    private final static String email = "test";
    private final static String otherEmail = "other";
    private final String title = "hi";
    private final String body = "hi";
    private final Map<String, Object> data = null;


    @Autowired
    public SendMessageServiceTest(SendMessageService sendMessageService, DBMessageBoxRepoHelper dbMessageBoxRepoHelper, FCMDeviceHelper fcmDeviceHelper, AccountHelper accountHelper, TestNotificationRepository testNotificationRepository, TestAccountRepository testAccountRepository, ThreadPoolTaskExecutor executor) {
        this.sendMessageService = sendMessageService;
        this.dbMessageBoxRepoHelper = dbMessageBoxRepoHelper;
        this.fcmDeviceHelper = fcmDeviceHelper;
        this.accountHelper = accountHelper;
        this.testNotificationRepository = testNotificationRepository;
        this.testAccountRepository = testAccountRepository;
        this.executor = executor;
    }

    @BeforeEach
    public void clearMessageBoxDb(){
        dbMessageBoxRepoHelper.deleteAllInBatch();
        accountHelper.deleteAccount();

        Account testAccount = accountHelper.createAccount(email);
        Account otherAccount = accountHelper.createAccount(otherEmail);

        List<String> testDevices = new ArrayList<>();
        testDevices.add("123");
        testDevices.add("124");
        testDevices.add("125");
        List<String> otherDevices = new ArrayList<>();
        otherDevices.add("126");
        otherDevices.add("127");

        for(String testDevice : testDevices){
            fcmDeviceHelper.createDevice(testAccount, testDevice, DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);
        }

        for(String otherDevice : otherDevices){
            fcmDeviceHelper.createDevice(otherAccount, otherDevice, DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);
        }
    }

    @AfterAll
    public static void deleteAccount(@Autowired AccountHelper accountHelper, @Autowired DBMessageBoxRepoHelper dbMessageBoxRepoHelper){
        accountHelper.deleteAccount();
        dbMessageBoxRepoHelper.deleteAllInBatch();
    }


    @Nested
    class SendToAllDevicesTest{

        @Test
        @DisplayName("모든 디바이스에 보낼 메시지가 박스에 들어가는 지 테스트")
        void testSendToAllDevices(){
            sendMessageService.sendToAllDevice(NotificationType.TEST, body, title, data, RequestLocation.TEST_BETWEEN_TIME, ZonedDateTime.now().plus(5, ChronoUnit.MINUTES));

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(5, dbMessageBoxRepoHelper.findAll().size()));
        }

        @Test
        @DisplayName("TEST 알림이 켜져있는 계정의 메시지만 박스에 들어가는 지 테스트")
        void testSendToTestOffDevices(){
            Notification otherNotification = testNotificationRepository.findByAccount(testAccountRepository.findByEmail(otherEmail));
            otherNotification.updateNotification(NotificationType.TEST, NotificationSetting.OFF);
            testNotificationRepository.save(otherNotification);

            sendMessageService.sendToAllDevice(NotificationType.TEST, body, title, data, RequestLocation.TEST_BETWEEN_TIME, ZonedDateTime.now().plus(5, ChronoUnit.MINUTES));

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(3, dbMessageBoxRepoHelper.findAll().size()));
        }

        @Test
        @DisplayName("특정 위치(나라)의 모든 디바이스에 보낼 메시지가 박스에 들어가는 지 테스트")
        void testSendToAllDevicesByLocation(){
            Account testAccount = accountHelper.createAccount("test2");
            fcmDeviceHelper.createDevice(testAccount, "456", DeviceType.IOS, RequestLocation.KOREA);

            sendMessageService.sendToAllDevice(NotificationType.TEST, body, title, data, RequestLocation.TEST_BETWEEN_TIME, ZonedDateTime.now().plus(5, ChronoUnit.MINUTES));

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(5, dbMessageBoxRepoHelper.findAll().size()));

            sendMessageService.sendToAllDevice(NotificationType.TEST, body, title, data, RequestLocation.KOREA, ZonedDateTime.now().plus(5, ChronoUnit.MINUTES));

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(6, dbMessageBoxRepoHelper.findAll().size()));
        }
    }

    @Nested
    class SendToDeviceTest{
        @Test
        @DisplayName("특정 계정의 TEST 알림이 켜져있을 경우, 메시지가 박스에 들어가는 지 테스트 ")
        void testSendToTestOnDevice(){
            Account account = testAccountRepository.findByEmail(email);
            sendMessageService.sendToDevice(account, NotificationType.TEST, body, title, data, RequestLocation.TEST_BETWEEN_TIME, ZonedDateTime.now());

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(3, dbMessageBoxRepoHelper.findAll().size()));
        }

        @Test
        @DisplayName("특정 계정의 TEST 알림이 꺼져있을 경우, 메시지가 박스에 안 들어가는 지 테스트 ")
        void testSendToTestOffDevice(){
            Notification otherNotification = testNotificationRepository.findByAccount(testAccountRepository.findByEmail(otherEmail));
            otherNotification.updateNotification(NotificationType.TEST, NotificationSetting.OFF);
            testNotificationRepository.save(otherNotification);

            Account account = testAccountRepository.findByEmail(otherEmail);

            sendMessageService.sendToDevice(account, NotificationType.TEST, body, title, data, RequestLocation.TEST_BETWEEN_TIME, ZonedDateTime.now());

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(0, dbMessageBoxRepoHelper.findAll().size()));

        }
    }



}
