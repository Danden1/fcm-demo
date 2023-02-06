package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.*;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.infra.DBMessageBox;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;


import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.event.RecordApplicationEvents;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@RecordApplicationEvents
public class MessageDistributorTest {

    @TestConfiguration
    public static class TestConfig {
        @Bean
        @Primary
        public Messenger mockMessenger() {
            Messenger messenger = mock(Messenger.class);
            doAnswer(new Answer<Void>() {
                public Void answer(InvocationOnMock invocation) throws IOException {
                    Message message = invocation.getArgument(0);

                    System.out.println("Valid Message");
                    System.out.println(messageMaker.getMessage(token));

                    return null;
                }
            }).when(messenger).deliverMessage(any(Message.class));

            return messenger;
        }
    }


    private final MessageDistributor messageDistributor;
    private final static MessageMaker messageMaker = new MessageMaker();
    private final DBMessageBox dbMessageBox;
    private final DBMessageBoxRepoHelper dbMessageBoxRepoHelper;


    private final static String email = "test";
    private final static String token = "1234";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;


    @Autowired
    public MessageDistributorTest(MessageDistributor distributor, DBMessageBox dbMessageBox, DBMessageBoxRepoHelper dbMessageBoxRepoHelper) {
        this.messageDistributor = distributor;
        this.dbMessageBox = dbMessageBox;
        this.dbMessageBoxRepoHelper = dbMessageBoxRepoHelper;
    }


    @BeforeEach
    public void beforeEach(){
        System.setOut(new PrintStream(outContent));
    }
    @AfterEach
    public void afterEach(){
        System.setOut(originalOut);
        dbMessageBoxRepoHelper.deleteAll();
    }



    @BeforeAll
    public static void createAccount(@Autowired AccountHelper accountHelper, @Autowired FCMDeviceHelper fcmDeviceHelper){
        Account account = accountHelper.createAccount(email);
        fcmDeviceHelper.createDevice(account, token, DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);
    }

    @AfterAll
    public static void deleteAccount(@Autowired AccountHelper accountHelper){
        accountHelper.deleteAccount();
    }



    @Nested
    @DisplayName("올바른 메시지인 경우 테스트")
    class ValidTest {
        @Test
        @DisplayName("메시지가 batch size(8)보다 박스에 많이 들어있는 경우 테스트.")
        public void testTakeOfLagerThanBatchMessageAndPush() throws InterruptedException {
            int repeat = 10;
            Message message = messageMaker.makeValidTestMessage(token);
            for(int i = 0; i < repeat; i++) {
                dbMessageBox.collectMessage(message);
            }
            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(10, dbMessageBoxRepoHelper.findAll().size()));

//        messageDistributor.takeOutMessages();

            dbMessageBox.eventMaker();

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> {
                        assertEquals(2, dbMessageBoxRepoHelper.findAll().size());
                        assertTrue(StringUtils.countMatches(outContent.toString(), "Valid Message") == 8);
                        assertTrue(StringUtils.countMatches(outContent.toString(), messageMaker.getMessage(token)) == 18);
                    });
        }

        @Test
        @DisplayName("메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트")
        public void testTakeOfLessThanBatchMessageAndPush() throws InterruptedException {
            int repeat = 3;
            Message message = messageMaker.makeValidTestMessage(token);
            for(int i = 0; i < repeat; i++) {
                dbMessageBox.collectMessage(message);
            }
            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(3, dbMessageBoxRepoHelper.findAll().size()));

//        messageDistributor.takeOutMessages();
            dbMessageBox.eventMaker();

            await().pollDelay(Duration.ofSeconds(2)).until(()->true);
            System.out.println(StringUtils.countMatches(outContent.toString(), "Valid Message"));
            await().atMost(1, SECONDS)
                    .untilAsserted(() -> {
                        assertEquals(0, dbMessageBoxRepoHelper.findAll().size());
                        assertTrue(StringUtils.countMatches(outContent.toString(), "Valid Message") == 3);
                        assertTrue(StringUtils.countMatches(outContent.toString(), messageMaker.getMessage(token)) == repeat*2);
                    });
        }
    }




    @Nested
    @DisplayName("파기할 메시지 테스트")
    class DestroyTest {
        @Test
        @DisplayName("제한 시간을 지난 메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트(박스에서 제거)")
        public void testTakeOfLessThanBatchOverTimeLimitMessageAndPush() throws InterruptedException {
            int repeat = 3;
            Message invalidMessage = messageMaker.makeOverTimeLimitMessage(token);

            for(int i = 0; i < repeat; i++) {
                dbMessageBox.collectMessage(invalidMessage);
            }
            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(3, dbMessageBoxRepoHelper.findAll().size()));

//        messageDistributor.takeOutMessages();
            dbMessageBox.eventMaker();
            await().atMost(1, SECONDS)
                    .untilAsserted(() -> {
                        assertEquals(0, dbMessageBoxRepoHelper.findAll().size());
                        assertTrue(StringUtils.countMatches(outContent.toString(), String.format("Destroy Message %s", messageMaker.getMessage(token))) == repeat);
                    });

        }

        @Test
        @DisplayName("Event 메시지가 batch size(8)보다 박스에 적에 들어있는 경우 테스트(event의 defalut 알림 설정은 off)")
        public void testTakeOfLessThanBatchEventMessageAndPush() throws InterruptedException {
            int repeat = 3;
            Message message = messageMaker.makeEventMessage(token);

            for(int i = 0; i < repeat; i++) {
                dbMessageBox.collectMessage(message);
            }
            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(3, dbMessageBoxRepoHelper.findAll().size()));

            dbMessageBox.eventMaker();

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> {
                        assertEquals(0, dbMessageBoxRepoHelper.findAll().size());
                        assertTrue(StringUtils.countMatches(outContent.toString(), String.format("Destroy Message %s", messageMaker.getMessage(token))) == repeat);
                    });
        }
    }






    @Nested
    @DisplayName("재전송할 메시지 테스트(다시 박스에 넣어야 함).")
    class ResendTest{

        @Test
        @DisplayName("푸시 시간을 벗어난 메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트(다시 박스에 넣어야 함)")
        public void testTakeOfLessThanBatchOverSendingTimeMessageAndPush() throws InterruptedException {
            int repeat = 3;
            Message message = messageMaker.makeOverSendingTimeMessage(token);
            for(int i = 0; i < repeat; i++) {
                dbMessageBox.collectMessage(message);
            }
            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(3, dbMessageBoxRepoHelper.findAll().size()));


//        messageDistributor.takeOutMessages();
            dbMessageBox.eventMaker();
            await().atMost(1, SECONDS)
                    .untilAsserted(() -> {
                        assertEquals(3, dbMessageBoxRepoHelper.findAll().size());
                        assertTrue(StringUtils.countMatches(outContent.toString(), String.format("Resend Message %s", messageMaker.getMessage(token))) == repeat);
                        assertTrue(StringUtils.countMatches(outContent.toString(), String.format("Collect Message %s", messageMaker.getMessage(token))) == repeat*2);
                    });
        }
        @Test
        @DisplayName("예약 메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트(다시 박스에 넣어야 함)")
        public void testTakeOfLessThanBatchReservationMessageAndPush() throws InterruptedException {
            int repeat = 3;
            Message message = messageMaker.makeReservatinMessage(token);
            for(int i = 0; i < repeat; i++) {
                dbMessageBox.collectMessage(message);
            }
            await().atMost(1, SECONDS)
                    .untilAsserted(() -> assertEquals(3, dbMessageBoxRepoHelper.findAll().size()));


//        messageDistributor.takeOutMessages();
            dbMessageBox.eventMaker();
            await().atMost(1, SECONDS)
                    .untilAsserted(() -> {
                        assertEquals(3, dbMessageBoxRepoHelper.findAll().size());
                        assertTrue(StringUtils.countMatches(outContent.toString(), String.format("Resend Message %s", messageMaker.getMessage(token))) == repeat);
                        assertTrue(StringUtils.countMatches(outContent.toString(), String.format("Collect Message %s", messageMaker.getMessage(token))) == repeat*2);
                    });
        }
    }

}
