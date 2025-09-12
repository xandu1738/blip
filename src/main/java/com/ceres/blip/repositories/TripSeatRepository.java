package com.ceres.blip.repositories;

import com.ceres.blip.models.database.TripSeatModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripSeatRepository extends JpaRepository<TripSeatModel, Long> {
}
