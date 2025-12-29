package com.ceres.blip.repositories;

import com.ceres.blip.models.database.PartnerModel;
import jakarta.mail.Part;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface PartnersRepository extends JpaRepository<PartnerModel, Long> {
    Optional<PartnerModel> findByPartnerName(String partnerName);
    Optional<PartnerModel> findByPartnerCode(String partnerCode);
    @Query("SELECT COUNT(p.id) AS count FROM PartnerModel p")
    Optional<Map<String,Object>> partnersCount();
}
