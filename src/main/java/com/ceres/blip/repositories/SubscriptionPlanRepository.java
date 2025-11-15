package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SubscriptionPlanModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlanModel, Long> {
    Optional<SubscriptionPlanModel> findByPlanCode(String planCode);
}
