package com.ceres.project.repositories;

import com.ceres.project.models.database.SystemUserModel;
import com.ceres.project.models.jpa_helpers.repository.JetRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SystemUserRepository extends JetRepository<SystemUserModel, Long> {
    Optional<SystemUserModel> findByUsername(String username);

    Optional<SystemUserModel> findByEmail(String email);

    Optional<SystemUserModel> findByUsernameOrEmail(String username, String email);
}

