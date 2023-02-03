package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageEvent {
    private final List<Message> messages;

    MessageEvent(List<Message> messages){
        this.messages = messages;
    }

    public List<Message> getMessages(){
        return messages;
    }
}
