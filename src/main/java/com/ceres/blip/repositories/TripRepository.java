package com.ceres.blip.repositories;

import com.ceres.blip.models.database.TripModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TripRepository extends JpaRepository<TripModel,Long> {
}
