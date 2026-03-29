package com.pablogb.psychologger.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSettings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "default_session_duration")
    private Integer defaultSessionDuration = 50;

    @Column(name = "default_session_price", precision = 10, scale = 2)
    private BigDecimal defaultSessionPrice;

    @Column(name = "show_inactive_birthdays")
    private Boolean showInactiveBirthdays = false;

    @Column(name = "transcription_language", length = 10)
    private String transcriptionLanguage;

    @Column(name = "ui_language", length = 10)
    private String uiLanguage;

    @Column(name = "date_format", length = 20)
    private String dateFormat;

    @Column(name = "time_format", length = 5)
    private String timeFormat = "24h";
}
