package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SubscriptionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SubscriptionsRepository extends JpaRepository<SubscriptionModel, Long> {
}
