package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.AccountHelper;
import com.aiforpet.tdogtdog.fcm.helper.TestAccountRepository;
import com.aiforpet.tdogtdog.fcm.helper.TestNotificationRepository;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.NotificationException;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.PushTimeException;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.TimeLimitException;
import com.aiforpet.tdogtdog.module.fcm.domain.validator.MessageNotificationValidator;
import com.aiforpet.tdogtdog.module.fcm.domain.validator.MessagePushTimeValidator;
import com.aiforpet.tdogtdog.module.fcm.domain.validator.MessageTimeLimitValidator;
import com.aiforpet.tdogtdog.module.fcm.domain.validator.MessageValidator;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MessageValidatorTest {

    private final TestAccountRepository testAccountRepository;
    private final NotificationRepository notificationRepository;
    private final static String email = "test";

    @Autowired
    public MessageValidatorTest(TestAccountRepository testAccountRepository, NotificationRepository notificationRepository) {
        this.testAccountRepository = testAccountRepository;
        this.notificationRepository = notificationRepository;
    }


    @BeforeAll
    public static void createAccount(@Autowired AccountHelper accountHelper){
        accountHelper.createAccount(email);
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
            MessageValidator validator = new MessageTimeLimitValidator();
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now().minus(30, ChronoUnit.MINUTES), RequestLocation.TEST_BETWEEN_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS, account);

            Message message = new Message("hi", "hi", null, receiver, constraint);


            assertThrows(TimeLimitException.class, () -> validator.assertValidMessage(message));


        }

        @Test
        @DisplayName("제한 시간 넘지 않았을 경우 테스트")
        public void testBeforeTimeLimit(){
            MessageValidator validator = new MessageTimeLimitValidator();
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now().plus(30, ChronoUnit.MINUTES), RequestLocation.TEST_BETWEEN_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS, account);

            Message message = new Message("hi", "hi", null, receiver, constraint);

            validator.assertValidMessage(message);

        }
    }

    @Nested
    class PushTimeTest{
        @Test
        @DisplayName("푸시 보내는 시간 안에 있을 경우 테스트")
        public void testBetweenSendingTime(){
            MessageValidator validator = new MessagePushTimeValidator();
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now(), RequestLocation.TEST_BETWEEN_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS, account);

            Message message = new Message("hi", "hi", null, receiver, constraint);


            validator.assertValidMessage(message);
        }

        @Test
        @DisplayName("푸시 보내는 시간을 벗어났을 경우 테스트")
        public void testAfterSendingTime(){
            MessageValidator validator = new MessagePushTimeValidator();
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now(), RequestLocation.TEST_OVER_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS, account);

            Message message = new Message("hi", "hi", null, receiver, constraint);


            assertThrows(PushTimeException.class, () -> validator.assertValidMessage(message));
        }
    }

    @Nested
    class NotificationValidatorTest{
        @Test
        @DisplayName("TEST 알림을 받는 계정에 TEST 알림이 왔을 경우 테스트")
        public void testOnNotification(){
            MessageValidator validator = new MessageNotificationValidator(notificationRepository);
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now(), RequestLocation.TEST_BETWEEN_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS, account);

            Message message = new Message("hi", "hi", null, receiver, constraint);

            validator.assertValidMessage(message);
        }

        @Test
        @DisplayName("TEST 알림을 받지 않는 계정에 TEST 알림이 왔을 경우 테스트")
        public void testOffNotification(){
            MessageValidator validator = new MessageNotificationValidator(notificationRepository);
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.EVENT, ZonedDateTime.now(), RequestLocation.TEST_BETWEEN_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS, account);

            Message message = new Message("hi", "hi", null, receiver, constraint);
            assertThrows(NotificationException.class, () -> validator.assertValidMessage(message));
        }
    }
}
