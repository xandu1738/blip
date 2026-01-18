package com.ceres.blip.repositories;

import com.ceres.blip.dtos.SubscriptionRequestModelDto;
import com.ceres.blip.models.database.SubscriptionRequestModel;
import com.ceres.blip.models.enums.SubscriptionRequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRequestRepository extends JpaRepository<SubscriptionRequestModel, Long> {
    Optional<SubscriptionRequestModel> findByPartnerCodeAndStatus(String partnerCode, SubscriptionRequestStatus status);
    @Query("""
            SELECT sm.id AS id,
                   sm.partnerCode AS partnerCode,
                   sm.subscriptionReference AS subscriptionReference,
                   sm.status AS status,
                   sm.requestedOn AS requestedOn,
                   sm.amountPaid AS amountPaid,
                   sp.name AS planName,
                   su.email AS requestedBy,
                   cu.email AS paymentConfirmedBy,
                   sm.paymentAmount AS paymentAmount,
                   sm.paymentConfirmed AS paymentConfirmed
               FROM SubscriptionRequestModel sm
                    LEFT JOIN SystemUserModel su ON su.id = sm.userId
                    LEFT JOIN SystemUserModel cu ON cu.id = sm.confirmedBy
                    LEFT JOIN SubscriptionPlanModel sp ON sp.id = sm.subscriptionPlan
            """)
    List<SubscriptionRequestModelDto> fetchSubscriptionRequests(int pageSize, int pageNumber);
}
