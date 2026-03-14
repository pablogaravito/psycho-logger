package com.pablogb.psychologger.model.entity;

import com.pablogb.psychologger.model.enums.PaymentPlanStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payment_plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentPlan extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "therapist_id", nullable = false)
    private User therapist;

    @Column(name = "total_sessions", nullable = false)
    private Integer totalSessions;

    @Column(name = "sessions_used")
    private Integer sessionsUsed = 0;

    @Column(name = "price_per_session", precision = 10, scale = 2)
    private BigDecimal pricePerSession;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    private PaymentPlanStatus status = PaymentPlanStatus.ACTIVE;

    @Column(name = "paid_at")
    private LocalDateTime paidAt;
}