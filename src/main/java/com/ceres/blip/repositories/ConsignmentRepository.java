package com.ceres.blip.repositories;

import com.ceres.blip.models.database.ConsignmentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConsignmentRepository extends JpaRepository<ConsignmentModel, Long> {
    Optional<ConsignmentModel> findByConsignmentNumber(String consignmentNumber);

    List<ConsignmentModel> findAllByCreatedBy(Long createdBy);
}
