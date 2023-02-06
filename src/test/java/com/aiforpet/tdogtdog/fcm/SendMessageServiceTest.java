package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.*;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.service.SendMessageService;
import org.junit.jupiter.api.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;


@SpringBootTest
public class SendMessageServiceTest {
    private final SendMessageService sendMessageService;
    private final DBMessageBoxRepoHelper dbMessageBoxRepoHelper;
    private final FCMDeviceHelper fcmDeviceHelper;
    private final AccountHelper accountHelper;
    private final TestNotificationRepository testNotificationRepository;
    private final TestAccountRepository testAccountRepository;

    @MockBean
    private MessageDistributor messageDistributor;

    private final static String email = "test";
    private final static String otherEmail = "other";

    private final static String token = "";
    private final String title = "hi";
    private final String body = "hi";
    private final Map<String, Object> data = null;

    private final static CopyOnWriteArrayList<Message> mockBox = new CopyOnWriteArrayList<>();

    @TestConfiguration
    public static class TestConfig {

        @Bean
        @Primary
        public MessageBox mockMessageBox() {
            MessageBox messageBox = mock(MessageBox.class);
            doAnswer(new Answer<Void>() {
                public Void answer(InvocationOnMock invocation) throws IOException {
                    Message message = invocation.getArgument(0);
                    mockBox.add(message);

                    return null;
                }
            }).when(messageBox).collectMessage(any(Message.class));

            return messageBox;
        }
    }


    @Autowired
    public SendMessageServiceTest(SendMessageService sendMessageService, DBMessageBoxRepoHelper dbMessageBoxRepoHelper, FCMDeviceHelper fcmDeviceHelper, AccountHelper accountHelper, TestNotificationRepository testNotificationRepository, TestAccountRepository testAccountRepository) {
        this.sendMessageService = sendMessageService;
        this.dbMessageBoxRepoHelper = dbMessageBoxRepoHelper;
        this.fcmDeviceHelper = fcmDeviceHelper;
        this.accountHelper = accountHelper;
        this.testNotificationRepository = testNotificationRepository;
        this.testAccountRepository = testAccountRepository;
    }

    @BeforeEach
    public void clearMessageBoxDb(){
        dbMessageBoxRepoHelper.deleteAllInBatch();
        accountHelper.deleteAccount();

        Account testAccount = accountHelper.createAccount(email);
        Account otherAccount = accountHelper.createAccount(otherEmail);

        List<String> testDevices = new ArrayList<>();
        testDevices.add(token);
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
        mockBox.clear();
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
            sendMessageService.sendToAllDevice(NotificationType.TEST, body, title, data, RequestLocation.TEST_BETWEEN_TIME, LocalDateTime.now().plus(5, ChronoUnit.MINUTES), LocalDateTime.now());

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(5, mockBox.size()));
        }

        @Test
        @DisplayName("TEST 알림이 켜져있는 계정의 메시지만 박스에 들어가는 지 테스트")
        void testSendToTestOffDevices(){
            NotificationSettings otherNotification = testNotificationRepository.findByAccount(testAccountRepository.findByEmail(otherEmail));
            otherNotification.updateNotification(NotificationType.TEST, NotificationControl.OFF);
            testNotificationRepository.save(otherNotification);

            sendMessageService.sendToAllDevice(NotificationType.TEST, body, title, data, RequestLocation.TEST_BETWEEN_TIME, LocalDateTime.now().plus(5, ChronoUnit.MINUTES), LocalDateTime.now());

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(3, mockBox.size()));
        }

        @Test
        @DisplayName("특정 위치(나라)의 모든 디바이스에 보낼 메시지가 박스에 들어가는 지 테스트")
        void testSendToAllDevicesByLocation(){
            Account testAccount = accountHelper.createAccount("test2");
            fcmDeviceHelper.createDevice(testAccount, "456", DeviceType.IOS, RequestLocation.KOREA);

            sendMessageService.sendToAllDevice(NotificationType.TEST, body, title, data, RequestLocation.TEST_BETWEEN_TIME, LocalDateTime.now().plus(5, ChronoUnit.MINUTES), LocalDateTime.now());

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(5, mockBox.size()));

            sendMessageService.sendToAllDevice(NotificationType.TEST, body, title, data, RequestLocation.KOREA, LocalDateTime.now().plus(5, ChronoUnit.MINUTES), LocalDateTime.now());

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(6, mockBox.size()));
        }
    }

    @Nested
    class SendToDeviceTest{
        @Test
        @DisplayName("특정 계정의 TEST 알림이 켜져있을 경우, 메시지가 박스에 들어가는 지 테스트 ")
        void testSendToTestOnDevice(){
            Account account = testAccountRepository.findByEmail(email);
            sendMessageService.sendToDevice(account, NotificationType.TEST, body, title, data, RequestLocation.TEST_BETWEEN_TIME, LocalDateTime.now().plus(5, ChronoUnit.MINUTES), LocalDateTime.now());

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(3, mockBox.size()));
        }

        @Test
        @DisplayName("특정 계정의 TEST 알림이 꺼져있을 경우, 메시지가 박스에 안 들어가는 지 테스트 ")
        void testSendToTestOffDevice(){
            NotificationSettings otherNotification = testNotificationRepository.findByAccount(testAccountRepository.findByEmail(otherEmail));
            otherNotification.updateNotification(NotificationType.TEST, NotificationControl.OFF);
            testNotificationRepository.save(otherNotification);

            Account account = testAccountRepository.findByEmail(otherEmail);

            sendMessageService.sendToDevice(account, NotificationType.TEST, body, title, data, RequestLocation.TEST_BETWEEN_TIME, LocalDateTime.now().plus(5, ChronoUnit.MINUTES), LocalDateTime.now());

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(0, mockBox.size()));

        }
    }



}
