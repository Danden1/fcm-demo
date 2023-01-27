package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.AccountHelper;
import com.aiforpet.tdogtdog.fcm.helper.FCMDeviceHelper;
import com.aiforpet.tdogtdog.fcm.helper.TestAccountRepository;
import com.aiforpet.tdogtdog.fcm.helper.TestFCMDeviceRepository;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.OldDeviceErasure;
import com.aiforpet.tdogtdog.module.fcm.domain.DeviceType;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDevice;
import com.aiforpet.tdogtdog.module.fcm.domain.RequestLocation;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class OldDeviceErasureTest {

    private final TestAccountRepository testAccountRepository;
    private final TestFCMDeviceRepository testFCMDeviceRepository;
    private final AccountHelper accountHelper;
    private final FCMDeviceHelper fcmDeviceHelper;
    private final OldDeviceErasure deviceScheduler;

    private final String email = "test";

    @Autowired
    public OldDeviceErasureTest(TestAccountRepository testAccountRepository, TestFCMDeviceRepository testFCMDeviceRepository, AccountHelper accountHelper, FCMDeviceHelper fcmDeviceHelper, OldDeviceErasure deviceScheduler) {
        this.testAccountRepository = testAccountRepository;
        this.testFCMDeviceRepository = testFCMDeviceRepository;
        this.accountHelper = accountHelper;
        this.fcmDeviceHelper = fcmDeviceHelper;
        this.deviceScheduler = deviceScheduler;
    }


    @AfterEach
    public void deleteAll(){
        testAccountRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("4개 device 중, 60일 지난 2개의 device 삭제되는지 테스트")
    public void testDeleteOldDevice(){
        List<String> devices = new ArrayList<>();
        Instant oldTime = Instant.now().minus(60, ChronoUnit.DAYS);
        oldTime.minus(5, ChronoUnit.MINUTES);

        devices.add("123");
        devices.add("124");
        devices.add("125");
        devices.add("126");


        Account account = accountHelper.createAccount(email);

        for(String device : devices) {
            fcmDeviceHelper.createDevice(account, device, DeviceType.IOS, RequestLocation.TEST_BETWEEN_TIME);
        }

        FCMDevice fcmDevice = testFCMDeviceRepository.findByDevice(devices.get(3));
        ReflectionTestUtils.setField(fcmDevice, "time", oldTime);
        testFCMDeviceRepository.save(fcmDevice);

        fcmDevice = testFCMDeviceRepository.findByDevice(devices.get(2));
        ReflectionTestUtils.setField(fcmDevice, "time", oldTime);
        testFCMDeviceRepository.save(fcmDevice);

        deviceScheduler.deleteOldDevices();

        assertEquals(2, testFCMDeviceRepository.findAll().size());
    }
}
