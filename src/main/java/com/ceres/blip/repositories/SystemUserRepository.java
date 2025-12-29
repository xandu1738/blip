package com.ceres.blip.repositories;

import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.models.jpa_helpers.repository.JetRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemUserRepository extends JpaRepository<SystemUserModel, Long> {
    Optional<SystemUserModel> findByUserName(String username);

    Optional<SystemUserModel> findByEmail(String email);

    Optional<SystemUserModel> findByUserNameOrEmail(String username, String email);
}

