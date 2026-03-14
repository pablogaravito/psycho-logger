package com.pablogb.psychologger.repository;

import com.pablogb.psychologger.model.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationRepository extends JpaRepository<Organization, Integer> {
}
