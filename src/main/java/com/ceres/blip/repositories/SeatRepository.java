package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SeatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<SeatModel, Long> {
    List<SeatModel> findAllByBusId(Long tripId);
}
