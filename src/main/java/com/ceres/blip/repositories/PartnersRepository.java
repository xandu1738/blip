package com.ceres.blip.repositories;

import com.ceres.blip.models.database.PartnerModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public interface PartnersRepository extends JpaRepository<PartnerModel, Long>,
        JpaSpecificationExecutor<PartnerModel> {
    Optional<PartnerModel> findByPartnerName(String partnerName);

    Optional<PartnerModel> findByPartnerCode(String partnerCode);

    Page<PartnerModel> findByArchivedFalse(Pageable pageable);

    @Query("SELECT COUNT(p.id) AS count FROM PartnerModel p WHERE p.archived = false")
    Optional<Map<String, Object>> partnersCount();
}
