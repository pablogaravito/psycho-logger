package com.pablogb.psychologger.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "monthly_snapshots")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlySnapshot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, name = "snapshot_year")
    private Integer year;

    @Column(nullable = false, name = "snapshot_month")
    private Integer month;

    @Column(name = "active_patients")
    private Integer activePatients = 0;

    @Column(name = "total_sessions")
    private Integer totalSessions = 0;

    @Column(name = "completed_sessions")
    private Integer completedSessions = 0;

    @Column(name = "total_collected", precision = 10, scale = 2)
    private BigDecimal totalCollected = BigDecimal.ZERO;

    @Column(name = "total_pending", precision = 10, scale = 2)
    private BigDecimal totalPending = BigDecimal.ZERO;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
