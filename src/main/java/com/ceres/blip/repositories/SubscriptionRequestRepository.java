package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SubscriptionRequestModel;
import com.ceres.blip.models.enums.SubscriptionRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface SubscriptionRequestRepository extends JpaRepository<SubscriptionRequestModel, Long> {
    Optional<SubscriptionRequestModel> findByPartnerCodeAndStatus(String partnerCode, SubscriptionRequestStatus status);
}
