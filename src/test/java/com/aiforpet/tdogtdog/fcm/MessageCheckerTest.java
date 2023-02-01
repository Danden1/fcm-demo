package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.*;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.NotificationChecker;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.ReservationChecker;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.SendingTimeChecker;
import com.aiforpet.tdogtdog.module.fcm.domain.checker.TimeLimitChecker;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MessageCheckerTest {

    private final MessageMaker messageMaker;
    private final FCMDeviceRepository fcmDeviceRepository;
    private final static String email = "test";

    @Autowired
    public MessageCheckerTest(FCMDeviceRepository fcmDeviceRepository) {
        this.messageMaker = new MessageMaker();
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

            Message message = messageMaker.makeOverTimeLimitMessage("123");

            assertTrue(timeLimitChecker.isDestroy(message));
        }

        @Test
        @DisplayName("제한 시간 넘지 않았을 경우 테스트")
        public void testBeforeTimeLimit(){
            TimeLimitChecker timeLimitChecker = new TimeLimitChecker();

            Message message = messageMaker.makeValidTestMessage("123");

            assertFalse(timeLimitChecker.isDestroy(message));

        }
    }

    @Nested
    class SendingTimeTest{
        @Test
        @DisplayName("푸시 보내는 시간 안에 있을 경우 테스트")
        public void testBetweenSendingTime(){
            SendingTimeChecker sendingTimeChecker = new SendingTimeChecker();

            Message message = messageMaker.makeValidTestMessage("123");

            assertFalse(sendingTimeChecker.isResend(message));
        }

        @Test
        @DisplayName("푸시 보내는 시간을 벗어났을 경우 테스트")
        public void testAfterSendingTime(){
            SendingTimeChecker sendingTimeChecker = new SendingTimeChecker();

            Message message = messageMaker.makeOverSendingTimeMessage("123");

            assertTrue(sendingTimeChecker.isResend(message));
        }
    }

    @Nested
    class ReservationTest{
        @Test
        @DisplayName("예약 시간이 현재인 경우 테스트")
        public void testBetweenSendingTime(){
            ReservationChecker sendingTimeChecker = new ReservationChecker();

            Message message = messageMaker.makeValidTestMessage("123");

            assertFalse(sendingTimeChecker.isResend(message));
        }

        @Test
        @DisplayName("예약 시간 전인 경우 테스트")
        public void testAfterSendingTime(){
            ReservationChecker sendingTimeChecker = new ReservationChecker();

            Message message = messageMaker.makeReservatinMessage("123");

            assertTrue(sendingTimeChecker.isResend(message));
        }
    }

    @Nested
    class NotificationValidatorTest{
        @Test
        @DisplayName("TEST 알림을 받는 계정에 TEST 알림이 왔을 경우 테스트")
        public void testOnNotification(){
            NotificationChecker notificationChecker = new NotificationChecker(fcmDeviceRepository);

            Message message = messageMaker.makeValidTestMessage("123");

            assertFalse(notificationChecker.isDestroy(message));
        }

        @Test
        @DisplayName("EVENT 알림을 받지 않는 계정에 EVENT 알림이 왔을 경우 테스트")
        public void testOffNotification(){
            NotificationChecker notificationChecker = new NotificationChecker(fcmDeviceRepository);

            Message message = messageMaker.makeEventMessage("123");

            assertTrue(notificationChecker.isDestroy(message));
        }
    }
}
