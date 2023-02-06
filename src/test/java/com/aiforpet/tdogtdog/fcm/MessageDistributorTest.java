package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.*;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.test.context.EmbeddedKafka;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.time.Duration;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@SpringBootTest
@EmbeddedKafka(ports=9092)
@DisplayName("kafaka 테스트. 단위 하나씩 테스트 해야함.")
class MessageDistributorTest {

    private final MessageDistributor messageDistributor;
    private final static MessageMaker messageMaker = new MessageMaker();
    private final MessageBox kafkaMessageBox;



    private final static String email = "test";
    private final static String token = "123";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());


    @Autowired
    public MessageDistributorTest(MessageBox kafkaMessageBox, MessageDistributor distributor) {
        this.messageDistributor = distributor;
        this.kafkaMessageBox = kafkaMessageBox;
    }

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


    @BeforeEach
    public void beforeEach(){
        System.setOut(new PrintStream(outContent));
    }
    @AfterEach
    public void afterEach(){
        System.setOut(originalOut);
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
    class ValidTest{

        @Test
        @DisplayName("메시지가 batch size(8)보다 박스에 많이 들어있는 경우 테스트.")
        public void testTakeOfLagerThanBatchMessageAndPush(){
            int repeat = 10;
            for(int i = 0; i < repeat; i++) {
                Message message = messageMaker.makeValidTestMessage(token);
                kafkaMessageBox.collectMessage(message);
            }


            await().atMost(1, SECONDS)
                    .untilAsserted(() -> {
                        assertTrue(StringUtils.countMatches(outContent.toString(), "Valid Message") >= 8);
                        assertTrue(StringUtils.countMatches(outContent.toString(), messageMaker.getMessage(token)) >= 8);
                    });
        }

        @Test
        @DisplayName("메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트.")
        public void testTakeOfLessThanBatchMessageAndPush(){
            int repeat = 3;

            for(int i = 0; i < repeat; i++) {
                Message message = messageMaker.makeValidTestMessage(token);
                kafkaMessageBox.collectMessage(message);
            }


            await().atMost(1, SECONDS)
                    .untilAsserted(() -> {
                        assertTrue(StringUtils.countMatches(outContent.toString(), "Valid Message") >= repeat);
                        assertTrue(StringUtils.countMatches(outContent.toString(), messageMaker.getMessage(token)) >= repeat);
                    });
        }
    }

    @Nested
    @DisplayName("파기할 메시지 테스트")
    class DestroyTest{
        @Test
        @DisplayName("Event 메시지가 batch size(8)보다 박스에 적에 들어있는 경우 테스트(event의 defalut 알림 설정은 off).")
        public void testTakeOfLessThanBatchEventMessageAndPush(){
            int repeat = 3;

            for(int i = 0; i < repeat; i++) {
                Message message = messageMaker.makeEventMessage(token);
                kafkaMessageBox.collectMessage(message);
            }

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> {
                        assertTrue(StringUtils.countMatches(outContent.toString(), String.format("Destroy Message %s", messageMaker.getMessage(token))) >= repeat);
                    });



        }

        @Test
        @DisplayName("제한 시간을 지난 메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트(박스에서 제거).")
        public void testTakeOfLessThanBatchOverTimeLimitMessageAndPush(){
            int repeat = 3;

            for(int i = 0; i < repeat; i++) {
                Message invalidMessage = messageMaker.makeOverTimeLimitMessage(token);
                kafkaMessageBox.collectMessage(invalidMessage);
            }

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> {
                        assertTrue(StringUtils.countMatches(outContent.toString(), String.format("Destroy Message %s", messageMaker.getMessage(token))) >= repeat);
                    });
        }
    }

    @Nested
    @DisplayName("재전송할 메시지 테스트(다시 박스에 넣어야 함).")
    class ResendTest{
        @Test
        @DisplayName("푸시 시간을 벗어난 메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트.")
        public void testTakeOfLessThanBatchOverSendingTimeMessageAndPush(){
            int repeat = 3;

            for(int i = 0; i < repeat; i++) {
                Message message = messageMaker.makeOverSendingTimeMessage(token);
                kafkaMessageBox.collectMessage(message);
            }


            await().atMost(1, SECONDS)
                    .untilAsserted(() -> {
                        assertTrue(StringUtils.countMatches(outContent.toString(), String.format("Resend Message %s", messageMaker.getMessage(token))) >= repeat*2);
                        assertTrue(StringUtils.countMatches(outContent.toString(), String.format("Collect Message %s", messageMaker.getMessage(token))) >= repeat*2);
                    });
        }

        @Test
        @DisplayName("예약 메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트.")
        public void testTakeOfLessThanBatchReservationMessageAndPush() throws InterruptedException {
            int repeat = 3;

            for(int i = 0; i < repeat; i++) {
                Message message = messageMaker.makeReservatinMessage(token);
                kafkaMessageBox.collectMessage(message);
            }

            await().atMost(1, SECONDS)
                    .untilAsserted(() -> {
                        assertTrue(StringUtils.countMatches(outContent.toString(), String.format("Resend Message %s", messageMaker.getMessage(token))) >= repeat*2);
                        assertTrue(StringUtils.countMatches(outContent.toString(), String.format("Collect Message %s", messageMaker.getMessage(token))) >= repeat*2);
                    });
        }
    }

}
