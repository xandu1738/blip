package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SubscriptionPlanModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionPlanRepository extends JpaRepository<SubscriptionPlanModel, Long> {
}
