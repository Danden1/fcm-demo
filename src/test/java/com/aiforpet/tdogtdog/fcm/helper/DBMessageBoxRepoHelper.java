package com.aiforpet.tdogtdog.fcm.helper;

import com.aiforpet.tdogtdog.module.fcm.infra.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DBMessageBoxRepoHelper extends JpaRepository<MessageEntity, Long> {
    public void deleteAllInBatch();
    public List<MessageEntity> findAll();
}
