package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.AccountHelper;
import com.aiforpet.tdogtdog.fcm.helper.DBMessageBoxRepoHelper;
import com.aiforpet.tdogtdog.fcm.helper.MessageMaker;
import com.aiforpet.tdogtdog.fcm.helper.TestAccountRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.infra.MessageBoxRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MessageBoxTest {

    private final MessageBoxRepository messageBoxRepository;
    private final MessageBox messageBox;
    private final DBMessageBoxRepoHelper dbMessageBoxRepoHelper;
    private final TestAccountRepository testAccountRepository;

    private final MessageMaker messageMaker;
    private static final String email = "test";


    @Autowired
    public MessageBoxTest(MessageBoxRepository messageBoxRepository, MessageBox messageBox, DBMessageBoxRepoHelper dbMessageBoxRepoHelper, TestAccountRepository testAccountRepository) {
        this.messageBoxRepository = messageBoxRepository;
        this.messageBox = messageBox;
        this.dbMessageBoxRepoHelper = dbMessageBoxRepoHelper;
        this.testAccountRepository = testAccountRepository;

        this.messageMaker = new MessageMaker();
    }

    @BeforeAll
    public static void createAccount(@Autowired AccountHelper accountHelper){
        accountHelper.createAccount(email);
    }

    @AfterAll
    public static void deleteAccount(@Autowired AccountHelper accountHelper){
        accountHelper.deleteAccount();
    }

    @AfterEach
    public void clearMessageBoxDb(){
        dbMessageBoxRepoHelper.deleteAllInBatch();
    }

    @Test
    @DisplayName("5개 메시지 메시지 박스에 들어가는 지 테스트")
    public void testPutMessage(){
        int repeatCollect = 5;

        for(int i = 0; i < repeatCollect; i++) {
            messageBox.collectMessage(messageMaker.makeValidTestMessage("123"));
        }

        await().atMost(1, SECONDS)
                .untilAsserted(() -> assertEquals(repeatCollect, dbMessageBoxRepoHelper.findAll().size()));
    }





}
