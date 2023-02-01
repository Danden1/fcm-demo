package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.fcm.domain.*;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.FCMErrorHandler;
import com.aiforpet.tdogtdog.module.fcm.domain.exception.FCMErrorType;
import org.springframework.stereotype.Component;

@Component
public class FCMErrorHandlerImpl implements FCMErrorHandler {

    private final FCMDeviceRepository fcmDeviceRepository;
    private final MessageBox messageBox;

    public FCMErrorHandlerImpl(FCMDeviceRepository fcmDeviceRepository, MessageBox messageBox) {
        this.fcmDeviceRepository = fcmDeviceRepository;
        this.messageBox = messageBox;
    }

    @Override
    public void handleError(FCMErrorType fcmExceptionType, Message message) {
        System.out.println(fcmExceptionType.getErrorMessage(message));
        if(fcmExceptionType == FCMErrorType.UNREGISTERED){
            fcmDeviceRepository.deleteByDevice(message.getReceiveDevice());
        }
        else if(fcmExceptionType == FCMErrorType.TIMEOUT || fcmExceptionType == FCMErrorType.INTERNAL_SERVER_ERROR){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        else if(fcmExceptionType == FCMErrorType.DEVICE_MESSAGE_RATE_EXCEEDED){
            Receiver receiver = new Receiver(message.getReceiveDevice(), message.getDeviceType());
            MessageConstraint messageConstraint = new MessageConstraint(message.getNotificationType(), message.getTimeLimit(), message.getRequestLocation(), message.getRequestTime().plusSeconds(1));
            Message delayMessage = new Message(message.getTitle(), message.getBody(), message.getData(),receiver, messageConstraint);

            messageBox.collectMessage(delayMessage);
        }
    }
}
