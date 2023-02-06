package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.AccountHelper;
import com.aiforpet.tdogtdog.fcm.helper.FCMDeviceHelper;
import com.aiforpet.tdogtdog.fcm.helper.MessageMaker;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.test.context.EmbeddedKafka;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;


import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@EmbeddedKafka(ports=9092)
public class MessageBoxTest {

    @MockBean
    private MessageDistributor messageDistributor;

    private final MessageBox messageBox;
    private final MessageMaker messageMaker;
    private static final String email = "test";

    private static String token = "3232";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private final PrintStream originalOut = System.out;


    @Autowired
    public MessageBoxTest(MessageBox messageBox) {
        this.messageBox = messageBox;
        this.messageMaker = new MessageMaker();
    }


    @BeforeAll
    public static void createAccount(@Autowired AccountHelper accountHelper, @Autowired FCMDeviceHelper fcmDeviceHelper){
        Account account = accountHelper.createAccount(email);
        fcmDeviceHelper.createDevice(account, "123", DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);

    }

    @AfterAll
    public static void deleteAccount(@Autowired AccountHelper accountHelper){
        accountHelper.deleteAccount();
    }

    @BeforeEach
    public void beforeEach(){
        System.setOut(new PrintStream(outContent));
    }
    @AfterEach
    public void afterEach(){
        System.setOut(originalOut);
    }


    @Test
    @DisplayName("5개 메시지 메시지 박스에 들어가는 지 테스트")
    public void testPutMessage(){
        int repeat = 5;

        for(int i = 0; i < repeat; i++) {
            messageBox.collectMessage(messageMaker.makeValidTestMessage(token));
        }

        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
                    assertTrue(StringUtils.countMatches(outContent.toString(), String.format("Collect Message %s", messageMaker.getMessage(token))) == repeat);
                });
    }

}
