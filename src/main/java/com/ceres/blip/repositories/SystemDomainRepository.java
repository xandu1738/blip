package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SystemDomainModel;
import com.ceres.blip.models.jpa_helpers.repository.JetRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SystemDomainRepository extends JpaRepository<SystemDomainModel, Long> {
}
