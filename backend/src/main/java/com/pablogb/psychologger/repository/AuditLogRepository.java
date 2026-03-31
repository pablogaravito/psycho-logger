package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.AuditLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface AuditLogRepository extends JpaRepository<AuditLog, Integer> {

    Page<AuditLog> findByOrganizationIdOrderByCreatedAtDesc(
            Integer orgId, Pageable pageable);

    Page<AuditLog> findByOrganizationIdAndEntityTypeOrderByCreatedAtDesc(
            Integer orgId, String entityType, Pageable pageable);

    Page<AuditLog> findByOrganizationIdAndUserIdOrderByCreatedAtDesc(
            Integer orgId, Integer userId, Pageable pageable);

    @Query("SELECT a FROM AuditLog a WHERE a.organization.id = :orgId " +
            "AND (:entityType IS NULL OR a.entityType = :entityType) " +
            "AND (:userId IS NULL OR a.user.id = :userId) " +
            "AND (:from IS NULL OR a.createdAt >= :from) " +
            "AND (:to IS NULL OR a.createdAt <= :to) " +
            "ORDER BY a.createdAt DESC")
    Page<AuditLog> findFiltered(
            @Param("orgId") Integer orgId,
            @Param("entityType") String entityType,
            @Param("userId") Integer userId,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable);
}
