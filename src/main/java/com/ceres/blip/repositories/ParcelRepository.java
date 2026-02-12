package com.ceres.blip.repositories;

import com.ceres.blip.models.database.ParcelModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ParcelRepository extends JpaRepository<ParcelModel, Long> {
    List<ParcelModel> findAllByCreatedBy(Long createdBy);

    List<ParcelModel> findAllByStatus(String status);
}
