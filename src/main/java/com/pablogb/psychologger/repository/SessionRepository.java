package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.domain.entity.SessionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SessionRepository extends JpaRepository<SessionEntity, Long> {

    @Query("select s from SessionEntity s join s.patients p where p.id = :patientId")
    Page<SessionEntity> getSessionsFromPatientPaginated(Long patientId, Pageable pageable);

    @Query("select s from SessionEntity s join s.patients p where p.id = :patientId")
    List<SessionEntity> getSessionsFromPatient(Long patientId);

    @Query("SELECT s FROM SessionEntity s WHERE " +
            "LOWER(s.themes) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(s.nextWeek) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<SessionEntity> findSessionsByKeyword(@Param("keyword") String keyword, Pageable pageable);


    /**
     *     @Query("SELECT m FROM Meeting m WHERE " +
     *            "LOWER(m.column1) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
     *            "LOWER(m.column2) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
     *            "LOWER(m.column3) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
     *     Page<Meeting> searchMeetings(@Param("searchTerm") String searchTerm, Pageable pageable);
     *
     *
     *
     *
     *     @Query("SELECT s FROM SessionEntity s WHERE " +
     *             "LOWER(s.themes) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
     *             "LOWER(s.content) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
     *             "LOWER(s.newtWeek) LIKE LOWER(CONCAT('%', :keyword, '%'))")
     */


}

