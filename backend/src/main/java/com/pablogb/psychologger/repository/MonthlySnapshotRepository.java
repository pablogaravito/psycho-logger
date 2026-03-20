package com.pablogb.psychologger.repository;


import com.pablogb.psychologger.model.entity.MonthlySnapshot;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface MonthlySnapshotRepository extends JpaRepository<MonthlySnapshot, Integer> {
    Optional<MonthlySnapshot> findByUserIdAndYearAndMonth(Integer userId, Integer year, Integer month);
    List<MonthlySnapshot> findByUserIdOrderByYearDescMonthDesc(Integer userId);
    boolean existsByUserIdAndYearAndMonth(Integer userId, Integer year, Integer month);
}
