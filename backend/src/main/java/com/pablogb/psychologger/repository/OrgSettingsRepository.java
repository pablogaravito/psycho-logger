package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.OrgSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface OrgSettingsRepository extends JpaRepository<OrgSettings, Integer> {
    Optional<OrgSettings> findByOrganizationId(Integer orgId);
}
