package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.AccountHelper;
import com.aiforpet.tdogtdog.fcm.helper.FCMDeviceHelper;
import com.aiforpet.tdogtdog.fcm.helper.MessageMaker;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import org.junit.jupiter.api.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.io.IOException;
import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

@SpringBootTest
@EmbeddedKafka(ports=9092)
public class MessageBoxTest {

    private final MessageBox messageBox;
    private final FCMDeviceHelper fcmDeviceHelper;
    private final MessageMaker messageMaker;
    private static final String email = "test";

    private static int size = 0;


    @Autowired
    public MessageBoxTest(MessageBox messageBox, FCMDeviceHelper fcmDeviceHelper) {
        this.messageBox = messageBox;
        this.fcmDeviceHelper = fcmDeviceHelper;

        this.messageMaker = new MessageMaker();
    }

//    @TestConfiguration
//    public static class TestConfig {
//
////        not work
//        @Bean
//        @Primary
//
//        public MessageDistributor mockDistributor() {
//            MessageDistributor messageDistributor = mock(MessageDistributor.class);
//            doAnswer(new Answer<Void>() {
//                @KafkaListener(topics = "fcm", groupId = "fcm")
//                public Void answer(InvocationOnMock invocation) throws IOException {
//                    List<Message> messages = invocation.getArgument(0);
//                    size = messages.size();
//
//
//                    return null;
//                }
//            }).when(messageDistributor).distributeMessages(any(List.class));
//
//            return messageDistributor;
//        }
//    }

    @BeforeAll
    public static void createAccount(@Autowired AccountHelper accountHelper, @Autowired FCMDeviceHelper fcmDeviceHelper){
        Account account = accountHelper.createAccount(email);
        fcmDeviceHelper.createDevice(account, "123", DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);

    }

    @AfterAll
    public static void deleteAccount(@Autowired AccountHelper accountHelper){
        accountHelper.deleteAccount();
    }

    @AfterEach
    public void clearMessageBoxDb(){

    }

    @Test
    @DisplayName("5개 메시지 메시지 박스에 들어가는 지 테스트")
    public void testPutMessage(){
        int repeatCollect = 5;

        for(int i = 0; i < repeatCollect; i++) {
            messageBox.collectMessage(messageMaker.makeValidTestMessage("123"));
        }

        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(repeatCollect, size));
    }





}
