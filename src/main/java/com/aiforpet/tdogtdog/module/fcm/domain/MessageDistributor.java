package com.aiforpet.tdogtdog.module.fcm.domain;

import java.util.List;

public interface MessageDistributor {
    void takeOutMessages(List<Message> messages);
    void distributeMessages(List<Message> messages);
}
