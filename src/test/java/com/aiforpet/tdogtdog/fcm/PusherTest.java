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

import java.util.List;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
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


    @Autowired
    public PusherTest(PusherImpl pusher, DBMessageBox dbMessageBox, DBMessageBoxRepoHelper dbMessageBoxRepoHelper, TestAccountRepository testAccountRepository) {
        this.pusher = pusher;
        this.dbMessageBox = dbMessageBox;
        this.dbMessageBoxRepoHelper = dbMessageBoxRepoHelper;
        this.testAccountRepository = testAccountRepository;
        this.messageMaker = new MessageMaker();
    }

    @AfterEach
    public void clearMessageBoxDb(){
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
        List<Message> messages = messageMaker.makeValidTestMessages(1, getAccount());

        for(Message message : messages){
            pusher.push(message);
        }
    }


    @Test
    @DisplayName("Event 메시지가 batch size(8)보다 박스에 적에 들어있는 경우 테스트(event의 defalut 알림 설정은 off)")
    public void testTakeOf5EventMessageAndPush() throws InterruptedException {
        List<Message> messages = messageMaker.makeEventMessages(5, getAccount());

        for(Message message : messages) {
            dbMessageBox.collectMessage(message);
        }
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(5, dbMessageBoxRepoHelper.findAll().size()));

        pusher.takeOfMessage();
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(0, dbMessageBoxRepoHelper.findAll().size()));
    }

    @Test
    @DisplayName("메시지가 batch size(8)보다 박스에 많이 들어있는 경우 테스트")
    public void testTakeOfLagerThan8MessageAndPush() throws InterruptedException {
        List<Message> messages = messageMaker.makeValidTestMessages(10,getAccount());
        for(Message message : messages) {
            dbMessageBox.collectMessage(message);
        }
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(10, dbMessageBoxRepoHelper.findAll().size()));

        pusher.takeOfMessage();
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(2, dbMessageBoxRepoHelper.findAll().size()));

    }
    @Test
    @DisplayName("메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트")
    public void testTakeOf5MessageAndPush() throws InterruptedException {
        List<Message> messages = messageMaker.makeValidTestMessages(5, getAccount());
        for(Message message : messages) {
            dbMessageBox.collectMessage(message);
        }
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(5, dbMessageBoxRepoHelper.findAll().size()));

        pusher.takeOfMessage();
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(0, dbMessageBoxRepoHelper.findAll().size()));

    }

    @Test
    @DisplayName("푸시 시간을 벗어난 메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트(다시 박스에 넣어야 함)")
    public void testTakeOf5OverSendingTimeMessageAndPush() throws InterruptedException {
        List<Message> messages = messageMaker.makeOverSendingTimeMessages(5, getAccount());
        for(Message message : messages) {
            dbMessageBox.collectMessage(message);
        }
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(5, dbMessageBoxRepoHelper.findAll().size()));


        pusher.takeOfMessage();
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(dbMessageBoxRepoHelper.findAll().size(), 5));

    }



    @Test
    @DisplayName("제한 시간을 지난 메시지가 batch size(8)보다 박스에 적게 들어있는 경우 테스트(박스에서 제거)")
    public void testTakeOf5OverTimeLimitMessageAndPush() throws InterruptedException {
        List<Message> invalidMessages = messageMaker.makeOverTimeLimitMessages(5, getAccount());

        for(Message message : invalidMessages) {
            dbMessageBox.collectMessage(message);
        }
        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(5, dbMessageBoxRepoHelper.findAll().size()));

        pusher.takeOfMessage();
        await().atMost(1, SECONDS)
                .untilAsserted(() ->  assertEquals(dbMessageBoxRepoHelper.findAll().size(), 0));

    }



    private Account getAccount(){
        return testAccountRepository.findByEmail(email);
    }


}
