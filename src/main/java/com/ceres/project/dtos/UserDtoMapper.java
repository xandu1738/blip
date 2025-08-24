package com.ceres.project.dtos;

import com.ceres.project.models.database.SystemRoleModel;
import com.ceres.project.models.database.SystemUserModel;
import com.ceres.project.repositories.SystemRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
@RequiredArgsConstructor
public class UserDtoMapper implements Function<SystemUserModel, UserDto> {
    private final SystemRoleRepository systemRoleRepository;

    @Override
    public UserDto apply(SystemUserModel systemUserModel) {

        SystemRoleModel role = systemRoleRepository.findByRoleCode(systemUserModel.getRoleCode())
                .orElseThrow(() -> new IllegalStateException("Role not found for code: " + systemUserModel.getRoleCode()));

        return new UserDto(
                systemUserModel.getId(),
                systemUserModel.getFirstName(),
                systemUserModel.getLastName(),
                systemUserModel.getEmail(),
                systemUserModel.getRoleCode(),
                role.getRoleDomain().name(),
                systemUserModel.getCreatedAt(),
                systemUserModel.getLastLoggedInAt(),
                systemUserModel.getIsActive(),
                systemUserModel.getUsername()
        );
    }
}
