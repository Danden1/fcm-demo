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


    private final ThreadPoolTaskExecutor executor;

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
            fcmDeviceHelper.createDevice(testAccount, testDevice, DeviceType.IOS);
        }

        for(String otherDevice : otherDevices){
            fcmDeviceHelper.createDevice(otherAccount, otherDevice, DeviceType.IOS);
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
        void testSendToAllDevices(){
            sendMessageService.sendToAllDevice(NotificationType.TEST, body, title, data, RequestLocation.TEST_BETWEEN_TIME, ZonedDateTime.now().plus(5, ChronoUnit.MINUTES));

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(5, dbMessageBoxRepoHelper.findAll().size()));
        }

        @Test
        void testSendToTestOffDevices(){
            Notification otherNotification = testNotificationRepository.findByAccount(testAccountRepository.findByEmail(otherEmail));
            otherNotification.updateNotification(NotificationType.TEST, NotificationSetting.OFF);
            testNotificationRepository.save(otherNotification);

            sendMessageService.sendToAllDevice(NotificationType.TEST, body, title, data, RequestLocation.TEST_BETWEEN_TIME, ZonedDateTime.now().plus(5, ChronoUnit.MINUTES));

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(3, dbMessageBoxRepoHelper.findAll().size()));
        }
    }

    @Nested
    class SendToDeviceTest{
        @Test
        void testSendToTestOnDevice(){
            Account account = testAccountRepository.findByEmail(email);
            sendMessageService.sendToDevice(account, NotificationType.TEST, body, title, data, RequestLocation.TEST_BETWEEN_TIME, ZonedDateTime.now());

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(3, dbMessageBoxRepoHelper.findAll().size()));
        }

        @Test
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
