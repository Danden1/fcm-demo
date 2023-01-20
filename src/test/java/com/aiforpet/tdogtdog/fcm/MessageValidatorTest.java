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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
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
        testNotificationRepository.deleteAllInBatch();
        testAccountRepository.deleteAllInBatch();
    }


    @Nested
    class TimeLimitTest{
        @Test
        public void testOverTimeLimit(){
            MessageValidator validator = new MessageTimeLimitValidator();
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now().minus(30, ChronoUnit.MINUTES), RequestLocation.KOREA);
            Receiver receiver = new Receiver("123", DeviceType.IOS, account);

            Message message = new Message("hi", "hi", null, receiver, constraint);


            assertThrows(TimeLimitException.class, () -> validator.assertValidMessage(message));


        }

        @Test
        public void testBeforeTimeLimit(){
            MessageValidator validator = new MessageTimeLimitValidator();
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now().plus(30, ChronoUnit.MINUTES), RequestLocation.KOREA);
            Receiver receiver = new Receiver("123", DeviceType.IOS, account);

            Message message = new Message("hi", "hi", null, receiver, constraint);

            validator.assertValidMessage(message);

        }
    }

    @Nested
    class PushTimeTest{
        @Test
        public void testBetweenSendingTime(){
            MessageValidator validator = new MessagePushTimeValidator();
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now(), RequestLocation.TEST_BETWEEN_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS, account);

            Message message = new Message("hi", "hi", null, receiver, constraint);


            validator.assertValidMessage(message);
        }

        @Test
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
        public void testOnNotification(){
            MessageValidator validator = new MessageNotificationValidator(notificationRepository);
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.TEST, ZonedDateTime.now(), RequestLocation.TEST_BETWEEN_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS, account);

            Message message = new Message("hi", "hi", null, receiver, constraint);

            validator.assertValidMessage(message);
        }

        @Test
        public void testOffNotification(){
            MessageValidator validator = new MessageNotificationValidator(notificationRepository);
            Account account = testAccountRepository.findByEmail(email);

            MessageConstraint constraint = new MessageConstraint(NotificationType.EVENT, ZonedDateTime.now(), RequestLocation.TEST_BETWEEN_TIME);
            Receiver receiver = new Receiver("123", DeviceType.IOS, account);

            Message message = new Message("hi", "hi", null, receiver, constraint);
            assertThrows(NotificationException.class, () -> validator.assertValidMessage(message));
        }
    }


    //Infra test
    @Test
    public void testValidatorsBeanLength(@Autowired List<MessageValidator> messageValidators){
        assertEquals(messageValidators.size(), 3);
    }

}
