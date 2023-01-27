package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDevice;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDeviceRepository;
import com.aiforpet.tdogtdog.module.fcm.domain.Notification;
import com.aiforpet.tdogtdog.module.fcm.domain.RequestLocation;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class FCMDeviceRepositoryImpl implements FCMDeviceRepository {

    private final JpaFCMDeviceRepository jpaFCMDeviceRepository;
    private final JpaNotificationRepository jpaNotificationRepository;

    public FCMDeviceRepositoryImpl(JpaFCMDeviceRepository jpaFCMDeviceRepository, JpaNotificationRepository jpaNotificationRepository) {
        this.jpaFCMDeviceRepository = jpaFCMDeviceRepository;
        this.jpaNotificationRepository = jpaNotificationRepository;
    }

    @Override
    public FCMDevice findAllByDevice(String device) {

        return jpaFCMDeviceRepository.findByDevice(device);
    }

    @Override
    public void deleteByDevice(String device) {
        jpaFCMDeviceRepository.deleteByDevice(device);
    }

    @Override
    public List<FCMDevice> findAllByRequestLocationAndAccountIn(RequestLocation requestLocation, List<Account> accounts) {
        return jpaFCMDeviceRepository.findAllByRequestLocationAndAccountIn(requestLocation, accounts);
    }

    @Override
    public void deleteByTimeLessThan(Instant time) {
        jpaFCMDeviceRepository.deleteByTimeLessThan(time);
    }


    @Override
    public void save(FCMDevice fcmDevice) {
        jpaFCMDeviceRepository.save(fcmDevice);
    }

    @Override
    public Notification findNotificationByDevice(String device) {
        Long notificationId = jpaFCMDeviceRepository.findNotificationIdByDevice(device);

        return jpaNotificationRepository.findNotificationById(notificationId);
//        return null;
    }


    @Override
    public List<FCMDevice> findAllByAccount(Account account){
        return jpaFCMDeviceRepository.findAllByAccount(account);
    }
}
