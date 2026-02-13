package com.ceres.blip.repositories;

import com.ceres.blip.models.database.PaymentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentsRepository extends JpaRepository<PaymentModel, Long> {
    Optional<PaymentModel> findByTransactionReference(String transactionReference);

    List<PaymentModel> findAllByStatus(String status);

    List<PaymentModel> findAllByPaymentMethod(String paymentMethod);
}
