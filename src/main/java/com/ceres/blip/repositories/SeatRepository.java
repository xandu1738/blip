package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SeatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<SeatModel,Long> {
}
