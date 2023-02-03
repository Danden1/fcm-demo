package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.*;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.infra.KafkaMessageBox;
import com.aiforpet.tdogtdog.module.fcm.infra.MessageDistributorImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@SpringBootTest
@EmbeddedKafka(ports=9092)
@ExtendWith(SpringExtension.class)
@DisplayName("kafaka 테스트. 눈으로 보고 확인해야 함. 로그 참고. 세부적인 테스트가 어려움.")
class MessageDistributorTest {

//    @InjectMocks
    private final MessageDistributor messageDistributor;
    private final MessageMaker messageMaker;
    private final MessageBox kafkaMessageBox;

    private final static String email = "test";
    private final static String token = "";

//    private final static ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private static StringBuffer outContent = new StringBuffer();
    private final PrintStream originalOut = System.out;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new ParameterNamesModule())
            .registerModule(new Jdk8Module())
            .registerModule(new JavaTimeModule());;


    @Autowired
    public MessageDistributorTest(MessageBox kafkaMessageBox, MessageDistributor distributor) {
        this.messageDistributor = distributor;
        this.kafkaMessageBox = kafkaMessageBox;
        this.messageMaker = new MessageMaker();
    }

//    @TestConfiguration
//    public static class TestConfig {
//
//        @Bean
//        @Primary
//        public Messenger mockMessenger() {
//            Messenger messenger = mock(Messenger.class);
//            doAnswer(new Answer<Void>() {
//                public Void answer(InvocationOnMock invocation) throws IOException {
//                    Message message = invocation.getArgument(0);
//
////                    System.out.println(String.format("%s %s %s %s%n",message.getBody(), message.getTitle(), message.getData(), message.getReceiveDevice()));
//                    outContent.append(String.format("%s %s %s %s%n",message.getBody(), message.getTitle(), message.getData(), message.getReceiveDevice()));
//                    return null;
//                }
//            }).when(messenger).deliverMessage(any(Message.class));
//
//            return messenger;
//        }
//    }


    @BeforeEach
    public void beforeEach(){
//        System.setOut(new PrintStream(outContent));
    }
    @AfterEach
    public void afterEach(){
//        System.setOut(originalOut);
        outContent = new StringBuffer();
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


    @Test
    @DisplayName("Event 메시지가 batch size(8)보다 박스에 적에 들어있는 경우 테스트(event의 defalut 알림 설정은 off)")
    public void testTakeOfLessThanBatchEventMessageAndPush() throws InterruptedException, JsonProcessingException {
        int repeat = 3;
        Message message = messageMaker.makeEventMessage(token);
        List<String> messages = new ArrayList<>();
        for(int i = 0; i < repeat; i++) {
            kafkaMessageBox.collectMessage(message);
//            messages.add(objectMapper.writeValueAsString(message));
        }

//        messageDistributor.takeOutMessages(messages);
        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
//                    assertEquals(0, dbMessageBoxRepoHelper.findAll().size());
                    assertEquals("", outContent.toString());
                });
    }

    @Test
    @DisplayName("메시지가 batch size(8)보다 박스에 많이 들어있는 경우 테스트.")
    public void testTakeOfLagerThanBatchMessageAndPush() throws InterruptedException {
        int repeat = 10;
        Message message = messageMaker.makeValidTestMessage(token);
        for(int i = 0; i < repeat; i++) {
            kafkaMessageBox.collectMessage(message);
        }

//        messageDistributor.takeOutMessages();
        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
                    assertEquals(String.format("%s%n", messageMaker.getMessage(token)).repeat(8), outContent.toString());
                });

    }
    @Test
    @DisplayName("메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트")
    public void testTakeOfLessThanBatchMessageAndPush() throws InterruptedException, JsonProcessingException {
        int repeat = 3;
        Message message = messageMaker.makeValidTestMessage(token);
        List<Message> messages = new ArrayList<>();
        for(int i = 0; i < repeat; i++) {
            kafkaMessageBox.collectMessage(message);
//            messages.add(message);
        }

//        messageDistributor.takeOutMessages(messages);
        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
                    assertEquals(String.format("%s%n", messageMaker.getMessage(token)).repeat(3), outContent.toString());
                });
    }

    @Test
    @DisplayName("푸시 시간을 벗어난 메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트(다시 박스에 넣어야 함)")
    public void testTakeOfLessThanBatchOverSendingTimeMessageAndPush() throws InterruptedException {
        int repeat = 3;
        Message message = messageMaker.makeOverSendingTimeMessage(token);
        for(int i = 0; i < repeat; i++) {
            kafkaMessageBox.collectMessage(message);
        }


//        messageDistributor.takeOutMessages();
        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
//                    assertEquals(3, dbMessageBoxRepoHelper.findAll().size());
                    assertEquals("", outContent.toString());
                });
    }

    @Test
    @DisplayName("예약 메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트(다시 박스에 넣어야 함)")
    public void testTakeOfLessThanBatchReservationMessageAndPush() throws InterruptedException {
        int repeat = 3;
        Message message = messageMaker.makeReservatinMessage(token);
        for(int i = 0; i < repeat; i++) {
            kafkaMessageBox.collectMessage(message);
        }

//        messageDistributor.takeOutMessages();
        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
//                    assertEquals(3, dbMessageBoxRepoHelper.findAll().size());
                    assertEquals("", outContent.toString());
                });
    }



    @Test
    @DisplayName("제한 시간을 지난 메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트(박스에서 제거)")
    public void testTakeOfLessThanBatchOverTimeLimitMessageAndPush() throws InterruptedException {
        int repeat = 3;
        Message invalidMessage = messageMaker.makeOverTimeLimitMessage(token);

        for(int i = 0; i < repeat; i++) {
            kafkaMessageBox.collectMessage(invalidMessage);
        }

//        messageDistributor.takeOutMessages();
        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
//                    assertEquals(0, dbMessageBoxRepoHelper.findAll().size());
                    assertEquals("", outContent.toString());
                });

    }
}
