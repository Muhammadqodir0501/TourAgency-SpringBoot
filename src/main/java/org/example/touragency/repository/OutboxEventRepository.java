package org.example.touragency.repository;

import org.example.touragency.enums.EventStatus;
import org.example.touragency.model.entity.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEvent, UUID> {

    List<OutboxEvent> findAllByStatusOrderByCreatedAtAsc(EventStatus status);
}
