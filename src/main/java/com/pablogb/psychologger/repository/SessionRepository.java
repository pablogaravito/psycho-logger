package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.SessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

    @Query("SELECT s FROM SessionEntity s JOIN s.patients p WHERE p.id = :patientId ORDER BY s.createdAt DESC")
    Page<SessionEntity> getSessionsFromPatientPaginated(Long patientId, Pageable pageable);

    @Query("SELECT s FROM SessionEntity s JOIN s.patients p WHERE p.id = :patientId ORDER BY s.createdAt DESC")
    List<SessionEntity> getSessionsFromPatient(Long patientId);

    Page<SessionEntity> findAllByOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT s FROM SessionEntity s WHERE " +
            "LOWER(s.themes) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.nextWeek) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            "ORDER BY s.createdAt DESC")
    Page<SessionEntity> findSessionsByKeyword(@Param("keyword") String keyword, Pageable pageable);
}

