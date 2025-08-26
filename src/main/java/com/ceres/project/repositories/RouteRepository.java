package com.ceres.project.repositories;

import com.ceres.project.models.database.RouteModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<RouteModel, Long> {
    List<RouteModel> findAllByPartnerCode(String partnerCode);
}
