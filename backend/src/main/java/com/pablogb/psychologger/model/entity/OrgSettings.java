package com.pablogb.psychologger.model.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "org_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrgSettings extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id", nullable = false)
    private Organization organization;

    @Column(name = "default_currency", length = 10)
    private String defaultCurrency = "USD";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "updated_by")
    private User updatedBy;

    @Column(name = "transcription_language", length = 10)
    private String transcriptionLanguage = "es";

    @Column(name = "ui_language", length = 10)
    private String uiLanguage = "es";

    @Column(name = "date_format", length = 20)
    private String dateFormat = "DD/MM/YYYY";
}
