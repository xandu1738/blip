package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SubscriptionModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<SubscriptionModel, Long> {
    @NativeQuery(value = """
            SELECT P.partner_name,M.name,S.start_date,S.end_date
            FROM subscriptions S
                     INNER JOIN partners P ON S.partner_code = P.partner_code
                     INNER JOIN modules M ON S.module_code = M.code
            WHERE P.partner_code = :partnerCode
            LIMIT :pageSize OFFSET :pageNumber
            """)
    Optional<Map<String, Object>> getSubscriptions(String partnerCode, int pageNumber, int pageSize);
}
