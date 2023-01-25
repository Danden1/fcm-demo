package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.AccountHelper;
import com.aiforpet.tdogtdog.fcm.helper.DBMessageBoxRepoHelper;
import com.aiforpet.tdogtdog.fcm.helper.MessageMaker;
import com.aiforpet.tdogtdog.fcm.helper.TestAccountRepository;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.infra.DBMessageBox;
import com.aiforpet.tdogtdog.module.fcm.infra.PusherImpl;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class PusherTest {

    private final Pusher pusher;
    private final MessageMaker messageMaker;
    private final DBMessageBox dbMessageBox;
    private final DBMessageBoxRepoHelper dbMessageBoxRepoHelper;
    private final TestAccountRepository testAccountRepository;

    private final static String email = "test";
    private final static String token = "123";

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;


    @Autowired
    public PusherTest(PusherImpl pusher, DBMessageBox dbMessageBox, DBMessageBoxRepoHelper dbMessageBoxRepoHelper, TestAccountRepository testAccountRepository) {
        this.pusher = pusher;
        this.dbMessageBox = dbMessageBox;
        this.dbMessageBoxRepoHelper = dbMessageBoxRepoHelper;
        this.testAccountRepository = testAccountRepository;
        this.messageMaker = new MessageMaker();
    }


    @BeforeEach
    public void beforeEach(){
        System.setOut(new PrintStream(outContent));
    }
    @AfterEach
    public void afterEach(){
        System.setOut(originalOut);
        dbMessageBoxRepoHelper.deleteAllInBatch();
    }



    @BeforeAll
    public static void createAccount(@Autowired AccountHelper accountHelper){
        accountHelper.createAccount(email);
    }

    @AfterAll
    public static void deleteAccount(@Autowired AccountHelper accountHelper){
        accountHelper.deleteAccount();
    }

    @Test
    @DisplayName("메시지 push 테스트(화면에 출력)")
    public void testPushMessage(){
        int repeat = 5;
        Message message = messageMaker.makeValidTestMessage(token, getAccount());

        for(int i = 0; i < repeat; i++){
            pusher.push(message);
        }

        await().atMost(1, SECONDS)
                .untilAsserted(() ->assertEquals(String.format("%s%n", messageMaker.getPushMessage(token)).repeat(5), outContent.toString()));
    }


    @Test
    @DisplayName("Event 메시지가 batch size(8)보다 박스에 적에 들어있는 경우 테스트(event의 defalut 알림 설정은 off)")
    public void testTakeOf5EventMessageAndPush() throws InterruptedException {
        int repeat = 5;
        Message message = messageMaker.makeEventMessage(token, getAccount());

        for(int i = 0; i < repeat; i++) {
            dbMessageBox.collectMessage(message);
        }
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(5, dbMessageBoxRepoHelper.findAll().size()));

        pusher.takeOutMessage();
        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
                    assertEquals(0, dbMessageBoxRepoHelper.findAll().size());
                    assertEquals("", outContent.toString());
                });

    }

    @Test
    @DisplayName("메시지가 batch size(8)보다 박스에 많이 들어있는 경우 테스트")
    public void testTakeOfLagerThan8MessageAndPush() throws InterruptedException {
        int repeat = 10;
        Message message = messageMaker.makeValidTestMessage(token,getAccount());
        for(int i = 0; i < repeat; i++) {
            dbMessageBox.collectMessage(message);
        }
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(10, dbMessageBoxRepoHelper.findAll().size()));

        pusher.takeOutMessage();
        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
                    assertEquals(2, dbMessageBoxRepoHelper.findAll().size());
                    assertEquals(String.format("%s%n", messageMaker.getPushMessage(token)).repeat(8), outContent.toString());
                });

    }
    @Test
    @DisplayName("메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트")
    public void testTakeOf5MessageAndPush() throws InterruptedException {
        int repeat = 5;
        Message message = messageMaker.makeValidTestMessage(token, getAccount());
        for(int i = 0; i < repeat; i++) {
            dbMessageBox.collectMessage(message);
        }
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(5, dbMessageBoxRepoHelper.findAll().size()));

        pusher.takeOutMessage();
        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
                    assertEquals(0, dbMessageBoxRepoHelper.findAll().size());
                    assertEquals(String.format("%s%n", messageMaker.getPushMessage(token)).repeat(5), outContent.toString());
                });

    }

    @Test
    @DisplayName("푸시 시간을 벗어난 메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트(다시 박스에 넣어야 함)")
    public void testTakeOf5OverSendingTimeMessageAndPush() throws InterruptedException {
        int repeat = 5;
        Message message = messageMaker.makeOverSendingTimeMessage(token, getAccount());
        for(int i = 0; i < repeat; i++) {
            dbMessageBox.collectMessage(message);
        }
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(5, dbMessageBoxRepoHelper.findAll().size()));


        pusher.takeOutMessage();
        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
                    assertEquals(5, dbMessageBoxRepoHelper.findAll().size());
                    assertEquals("", outContent.toString());
                });
    }



    @Test
    @DisplayName("제한 시간을 지난 메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트(박스에서 제거)")
    public void testTakeOf5OverTimeLimitMessageAndPush() throws InterruptedException {
        int repeat = 5;
        Message invalidMessage = messageMaker.makeOverTimeLimitMessage(token, getAccount());

        for(int i = 0; i < repeat; i++) {
            dbMessageBox.collectMessage(invalidMessage);
        }
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(5, dbMessageBoxRepoHelper.findAll().size()));

        pusher.takeOutMessage();
        await().atMost(1, SECONDS)
                .untilAsserted(() -> {
                    assertEquals(0, dbMessageBoxRepoHelper.findAll().size());
                    assertEquals("", outContent.toString());
                });

    }



    private Account getAccount(){
        return testAccountRepository.findByEmail(email);
    }


}
