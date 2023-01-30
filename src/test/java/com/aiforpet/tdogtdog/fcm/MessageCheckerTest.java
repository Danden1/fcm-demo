package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.AccountHelper;
import com.aiforpet.tdogtdog.fcm.helper.FCMDeviceHelper;
import com.aiforpet.tdogtdog.fcm.helper.TestAccountRepository;
import com.aiforpet.tdogtdog.fcm.helper.TestNotificationRepository;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.NotificationChecker;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.SendingTimeChecker;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.TimeLimitChecker;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MessageCheckerTest {

    private final TestAccountRepository testAccountRepository;
    private final FCMDeviceRepository fcmDeviceRepository;
    private final static String email = "test";

    @Autowired
    public MessageCheckerTest(TestAccountRepository testAccountRepository, FCMDeviceRepository fcmDeviceRepository) {
        this.testAccountRepository = testAccountRepository;
        this.fcmDeviceRepository = fcmDeviceRepository;
    }


    @BeforeAll
    public static void createAccount(@Autowired AccountHelper accountHelper, @Autowired FCMDeviceHelper fcmDeviceHelper){
        Account account = accountHelper.createAccount(email);
        fcmDeviceHelper.createDevice(account, "123", DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);
    }

    @AfterAll
    public static void deleteAccount(@Autowired TestAccountRepository testAccountRepository, @Autowired TestNotificationRepository testNotificationRepository){
        testAccountRepository.deleteAllInBatch();
    }


    @Nested
    class TimeLimitTest{
        @Test
        @DisplayName("제한 시간 넘어갔을 경우 테스트")
        public void testOverTimeLimit(){
            TimeLimitChecker timeLimitChecker = new TimeLimitChecker();
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now().minus(30, ChronoUnit.MINUTES), RequestLocation.TEST_BETWEEN_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS);

            Message message = new Message("hi", "hi", null, receiver, constraint);


            assertTrue(timeLimitChecker.isDestroy(message));
        }

        @Test
        @DisplayName("제한 시간 넘지 않았을 경우 테스트")
        public void testBeforeTimeLimit(){
            TimeLimitChecker timeLimitChecker = new TimeLimitChecker();
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now().plus(30, ChronoUnit.MINUTES), RequestLocation.TEST_BETWEEN_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS);

            Message message = new Message("hi", "hi", null, receiver, constraint);

            assertFalse(timeLimitChecker.isDestroy(message));

        }
    }

    @Nested
    class SendingTimeTest{
        @Test
        @DisplayName("푸시 보내는 시간 안에 있을 경우 테스트")
        public void testBetweenSendingTime(){
            SendingTimeChecker sendingTimeChecker = new SendingTimeChecker();
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now(), RequestLocation.TEST_BETWEEN_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS);

            Message message = new Message("hi", "hi", null, receiver, constraint);


            assertFalse(sendingTimeChecker.isResend(message));
        }

        @Test
        @DisplayName("푸시 보내는 시간을 벗어났을 경우 테스트")
        public void testAfterSendingTime(){
            SendingTimeChecker sendingTimeChecker = new SendingTimeChecker();
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now(), RequestLocation.TEST_OVER_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS);

            Message message = new Message("hi", "hi", null, receiver, constraint);


            assertTrue(sendingTimeChecker.isResend(message));
        }
    }

    @Nested
    class NotificationValidatorTest{
        @Test
        @DisplayName("TEST 알림을 받는 계정에 TEST 알림이 왔을 경우 테스트")
        public void testOnNotification(){
            NotificationChecker notificationChecker = new NotificationChecker(fcmDeviceRepository);
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now(), RequestLocation.TEST_BETWEEN_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS);

            Message message = new Message("hi", "hi", null, receiver, constraint);

            assertFalse(notificationChecker.isDestroy(message));
        }

        @Test
        @DisplayName("TEST 알림을 받지 않는 계정에 TEST 알림이 왔을 경우 테스트")
        public void testOffNotification(){
            NotificationChecker notificationChecker = new NotificationChecker(fcmDeviceRepository);
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.EVENT, ZonedDateTime.now(), RequestLocation.TEST_BETWEEN_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS);

            Message message = new Message("hi", "hi", null, receiver, constraint);
            assertTrue(notificationChecker.isDestroy(message));
        }
    }
}
