package com.aiforpet.tdogtdog.module.fcm.domain;

public interface Pusher {
    public void push(Message message);
    public void takeOutMessage();
}
