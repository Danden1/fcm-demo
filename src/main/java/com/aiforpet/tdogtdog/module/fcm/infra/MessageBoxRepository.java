package com.aiforpet.tdogtdog.module.fcm.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageBoxRepository extends JpaRepository<MessageEntity, Long> {
    public List<MessageEntity> findTop8ByOrderByIdAsc();
    public void delete(MessageEntity messageEntity);
}
