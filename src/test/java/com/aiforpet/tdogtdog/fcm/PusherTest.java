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

    @BeforeEach
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
    public void testPushMessage(){
        List<Message> messages = messageMaker.makeValidTestMessages(1, getAccount());

        for(Message message : messages){
            pusher.push(message);
        }
    }


    @Test
    public void testTakeOf5EventMessageAndPush() throws InterruptedException {
        List<Message> messages = messageMaker.makeEventMessages(5, getAccount());
        for(Message message : messages) {
            dbMessageBox.collectMessage(message);
        }

        pusher.takeOfMessage();

        assertEquals(dbMessageBoxRepoHelper.findAll().size(), 0);
    }

    @Test
    public void testTakeOfLagerThan8MessageAndPush() throws InterruptedException { //maximum batch size is 8
        List<Message> messages = messageMaker.makeValidTestMessages(10,getAccount());
        for(Message message : messages) {
            dbMessageBox.collectMessage(message);
        }

        pusher.takeOfMessage();

        assertEquals(dbMessageBoxRepoHelper.findAll().size(), 2);
    }
    @Test
    public void testTakeOf5MessageAndPush() throws InterruptedException {
        List<Message> messages = messageMaker.makeValidTestMessages(5, getAccount());
        for(Message message : messages) {
            dbMessageBox.collectMessage(message);
        }

        pusher.takeOfMessage();

        assertEquals(dbMessageBoxRepoHelper.findAll().size(), 0);
    }

    @Test
    public void testTakeOf5OverSendingTimeMessageAndPush() throws InterruptedException {
        List<Message> messages = messageMaker.makeOverSendingTimeMessages(5, getAccount());
        for(Message message : messages) {
            dbMessageBox.collectMessage(message);
        }

        pusher.takeOfMessage();

        assertEquals(dbMessageBoxRepoHelper.findAll().size(), 5);
    }



    @Test
    public void testTakeOf5OverTimeLimitMessageAndPush() throws InterruptedException {
        List<Message> invalidMessages = messageMaker.makeOverTimeLimitMessages(5, getAccount());

        for(Message message : invalidMessages) {
            dbMessageBox.collectMessage(message);
        }

        pusher.takeOfMessage();

        assertEquals(dbMessageBoxRepoHelper.findAll().size(), 0);
    }



    private Account getAccount(){
        return testAccountRepository.findByEmail(email);
    }


}
