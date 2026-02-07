package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SystemDomainModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemDomainRepository extends JpaRepository<SystemDomainModel, Long> {
    Optional<SystemDomainModel> findByDomainName(String domain);
}
