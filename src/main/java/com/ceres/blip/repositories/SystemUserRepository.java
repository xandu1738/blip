package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.models.jpa_helpers.repository.JetRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUserModel, Long> {

    Optional<SystemUserModel> findByEmail(String email);

    @Query("SELECT COUNT(u.id) AS count FROM SystemUserModel u")
    Optional<Map<String,Object>> userCount();
}

