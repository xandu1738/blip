package com.ceres.blip.repositories;

import com.ceres.blip.models.database.RouteModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<RouteModel, Long> {
    Page<RouteModel> findAllByPartnerCode(String partnerCode, Pageable pageable);
}
