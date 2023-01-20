package com.aiforpet.tdogtdog.module.fcm.domain;

import java.util.List;

public interface Pusher {
    public void push(Message message);
    public void takeOfMessage();
}
