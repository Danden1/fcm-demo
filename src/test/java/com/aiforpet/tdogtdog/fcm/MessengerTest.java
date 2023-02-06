package com.aiforpet.tdogtdog.fcm;


import com.aiforpet.tdogtdog.fcm.helper.AccountHelper;
import com.aiforpet.tdogtdog.fcm.helper.FCMDeviceHelper;
import com.aiforpet.tdogtdog.fcm.helper.MessageMaker;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.infra.KafkaConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.junit.jupiter.api.*;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class MessengerTest {

    private final static String email = "test";
    private final static String token = "";

    private final Messenger messenger;

    @MockBean
    private MessageDistributor messageDistributor;
    @MockBean
    private MessageBox messageBox;
    @MockBean
    private ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory;
    @MockBean
    private KafkaAdmin kafkaAdmin;
    @MockBean
    private NewTopic newTopic;
    @MockBean
    private ProducerFactory<String, String> producerFactory;
    @MockBean
    private KafkaTemplate<String, String> kafkaTemplate;
    @MockBean
    private  ConsumerFactory<? super String, ? super String> consumerFactory;


    private final static ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final static PrintStream originalOut = System.out;

    @Autowired
    public MessengerTest(Messenger messenger) {
        this.messenger = messenger;
    }


    @BeforeAll
    public static void createAccount(@Autowired AccountHelper accountHelper, @Autowired FCMDeviceHelper fcmDeviceHelper){
        Account account = accountHelper.createAccount(email);
        fcmDeviceHelper.createDevice(account, token, DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);
    }

    @BeforeEach
    public void beforeEach(){
        System.setOut(new PrintStream(outContent));
    }
    @AfterEach
    public void afterEach(){
        System.setOut(originalOut);
    }

    @AfterAll
    public static void deleteAccount(@Autowired AccountHelper accountHelper){
        accountHelper.deleteAccount();
    }

    @Test
    @DisplayName("Push 테스트")
    public void testValidMessage(){
        MessageMaker messageMaker = new MessageMaker();

        Message message = messageMaker.makeValidTestMessage(token);

        messenger.deliverMessage(message);

        assertTrue(outContent.toString().contains("Push Success"));
    }

    @Test
    @DisplayName("비정상적인 token 테스트")
    public void testInValidToken(){
        MessageMaker messageMaker = new MessageMaker();

        Message message = messageMaker.makeValidTestMessage("123");

        messenger.deliverMessage(message);

        assertTrue(outContent.toString().contains("Invalid Token"));
    }
}
