package com.aiforpet.tdogtdog.fcm;

import com.aiforpet.tdogtdog.fcm.helper.AccountHelper;
import com.aiforpet.tdogtdog.fcm.helper.FCMDeviceHelper;
import com.aiforpet.tdogtdog.fcm.helper.TestAccountRepository;
import com.aiforpet.tdogtdog.fcm.helper.TestFCMDeviceRepository;
import com.aiforpet.tdogtdog.module.account.Account;
import com.aiforpet.tdogtdog.module.fcm.domain.DeviceScheduler;
import com.aiforpet.tdogtdog.module.fcm.domain.DeviceType;
import com.aiforpet.tdogtdog.module.fcm.domain.FCMDevice;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
public class DeviceSchedulerTest {

    private final TestAccountRepository testAccountRepository;
    private final TestFCMDeviceRepository testFCMDeviceRepository;
    private final AccountHelper accountHelper;
    private final FCMDeviceHelper fcmDeviceHelper;
    private final DeviceScheduler deviceScheduler;

    private final String email = "test";

    public DeviceSchedulerTest(TestAccountRepository testAccountRepository, TestFCMDeviceRepository testFCMDeviceRepository, AccountHelper accountHelper, FCMDeviceHelper fcmDeviceHelper, DeviceScheduler deviceScheduler) {
        this.testAccountRepository = testAccountRepository;
        this.testFCMDeviceRepository = testFCMDeviceRepository;
        this.accountHelper = accountHelper;
        this.fcmDeviceHelper = fcmDeviceHelper;
        this.deviceScheduler = deviceScheduler;
    }

    public void deleteAll(){
        testAccountRepository.deleteAllInBatch();
    }

    @Test
    public void testDeleteOldDevice(){
        List<String> devices = new ArrayList<>();
        Instant oldTime = Instant.now().minus(2, ChronoUnit.MONTHS);
        oldTime.minus(1, ChronoUnit.MINUTES);

        devices.add("123");
        devices.add("124");
        devices.add("125");
        devices.add("126");


        Account account = accountHelper.createAccount(email);

        for(String device : devices) {
            fcmDeviceHelper.createDevice(account, device, DeviceType.IOS);
        }

        FCMDevice fcmDevice = testFCMDeviceRepository.findByDevice(devices.get(3));
        ReflectionTestUtils.setField(fcmDevice, "time", oldTime);
        testFCMDeviceRepository.save(fcmDevice);

        fcmDevice = testFCMDeviceRepository.findByDevice(devices.get(2));
        ReflectionTestUtils.setField(fcmDevice, "time", oldTime);
        testFCMDeviceRepository.save(fcmDevice);

        deviceScheduler.deleteOldDevice();

        assertEquals(2, testFCMDeviceRepository.findAll());
    }
}
