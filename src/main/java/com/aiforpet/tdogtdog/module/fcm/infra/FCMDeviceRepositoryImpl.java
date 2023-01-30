package com.aiforpet.tdogtdog.module.fcm.infra;

import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.*;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class FCMDeviceRepositoryImpl implements FCMDeviceRepository {

    private final JpaFCMDeviceRepository jpaFCMDeviceRepository;
    private final JpaNotificationSettingsRepository jpaNotificationRepository;

    public FCMDeviceRepositoryImpl(JpaFCMDeviceRepository jpaFCMDeviceRepository, JpaNotificationSettingsRepository jpaNotificationRepository) {
        this.jpaFCMDeviceRepository = jpaFCMDeviceRepository;
        this.jpaNotificationRepository = jpaNotificationRepository;
    }

    @Override
    public FCMDevice findByDevice(String device) {

        return jpaFCMDeviceRepository.findByDevice(device);
    }

    @Override
    public void deleteByDevice(String device) {
        jpaFCMDeviceRepository.deleteByDevice(device);
    }

    @Override
    public List<FCMDevice> findAllByRequestLocationAndNotificationSettings_AvailableNotificationContains(RequestLocation requestLocation, NotificationType notificationType){
        return jpaFCMDeviceRepository.findAllByRequestLocationAndNotificationSettings_AvailableNotificationContains(requestLocation, notificationType);
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
    public NotificationSettings findNotificationSettingsByDevice(String device) {
        Long notificationId = jpaFCMDeviceRepository.findNotificationIdByDevice(device);

        return jpaNotificationRepository.findNotificationById(notificationId);
    }


    @Override
    public List<FCMDevice> findAllByAccount(Account account){
        return jpaFCMDeviceRepository.findAllByAccount(account);
    }
}
