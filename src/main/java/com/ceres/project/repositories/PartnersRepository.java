package com.ceres.project.repositories;

import com.ceres.project.models.database.PartnerModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnersRepository extends JpaRepository<PartnerModel, Long> {
    Optional<PartnerModel> findByPartnerName(String partnerName);

    Optional<PartnerModel> findByPartnerCode(String partnerCode);
}
