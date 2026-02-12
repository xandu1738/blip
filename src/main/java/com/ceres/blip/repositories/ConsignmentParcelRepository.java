package com.ceres.blip.repositories;

import com.ceres.blip.models.database.ConsignmentParcelModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConsignmentParcelRepository extends JpaRepository<ConsignmentParcelModel, Long> {
    List<ConsignmentParcelModel> findAllByConsignmentId(Long consignmentId);

    void deleteByConsignmentIdAndParcelId(Long consignmentId, Long parcelId);
}
