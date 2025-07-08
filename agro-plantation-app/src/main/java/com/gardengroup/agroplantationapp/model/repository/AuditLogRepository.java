package com.gardengroup.agroplantationapp.model.repository;

import com.gardengroup.agroplantationapp.model.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}
