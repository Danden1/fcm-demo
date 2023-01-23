package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.DBMessageBoxRepoHelper;
import com.aiforpet.tdogtdog.fcm.helper.MessageMaker;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.infra.MessageBoxRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class MessageBoxTest {

    private final MessageBoxRepository messageBoxRepository;
    private final MessageBox messageBox;
    private final DBMessageBoxRepoHelper dbMessageBoxRepoHelper;

    private final MessageMaker messageMaker;


    @Autowired
    public MessageBoxTest(MessageBoxRepository messageBoxRepository, MessageBox messageBox, DBMessageBoxRepoHelper dbMessageBoxRepoHelper) {
        this.messageBoxRepository = messageBoxRepository;
        this.messageBox = messageBox;
        this.dbMessageBoxRepoHelper = dbMessageBoxRepoHelper;

        this.messageMaker = new MessageMaker();
    }

    @BeforeEach
    @AfterEach
    public void clearMessageBoxDb(){
        dbMessageBoxRepoHelper.deleteAllInBatch();
    }
    @Test
    public void testPutMessage(){
        int repeatCollect = 5;

        List<Message> messages = messageMaker.makeValidTestMessages(repeatCollect, new Account());

        for(Message message : messages) {
            messageBox.collectMessage(message);
        }

        assertEquals(repeatCollect, dbMessageBoxRepoHelper.findAll().size());
    }





}
