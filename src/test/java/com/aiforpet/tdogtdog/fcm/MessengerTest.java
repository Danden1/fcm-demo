package com.aiforpet.tdogtdog.fcm;


import com.aiforpet.tdogtdog.fcm.helper.AccountHelper;
import com.aiforpet.tdogtdog.fcm.helper.FCMDeviceHelper;
import com.aiforpet.tdogtdog.fcm.helper.MessageMaker;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.DeviceType;
import com.aiforpet.tdogtdog.module.fcm.domain.Message;
import com.aiforpet.tdogtdog.module.fcm.domain.Messenger;
import com.aiforpet.tdogtdog.module.fcm.domain.RequestLocation;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MessengerTest {

    private final static String email = "test";
    private final static String token = "";

    private final Messenger messenger;

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

        assertEquals(String.format("%s%n", messageMaker.getPushMessage(token)), outContent.toString());
    }
}
