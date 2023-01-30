package com.aiforpet.tdogtdog.module.fcm.domain;

import java.util.List;

public interface MessageDistributor {
    void takeOutMessages();
    void distributeMessages(List<Message> messages);
}
