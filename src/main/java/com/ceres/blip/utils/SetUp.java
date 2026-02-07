package com.ceres.blip.utils;

import com.ceres.blip.models.database.*;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.models.enums.DefaultPermissions;
import com.ceres.blip.models.enums.DefaultRoles;
import com.ceres.blip.repositories.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * This class runs on every app boot to set up all the defaults likes permissions, domains, etc.
 * To add more actions that shall always run on app start, create a method in here and
 * annotate it with @PostConstruct or chain i to an annotated one
 */
@Slf4j
@Service
public class SetUp {
    private static final String RAW_ADMIN_PASSWORD = "@Secure1234";
    private final SystemRolePermissionRepository systemRolePermissionRepository;
    private final SystemUserRepository systemUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final SystemDomainRepository domainRepository;
    private final SystemPermissionRepository permissionRepository;
    private final SystemRoleRepository roleRepository;

    public SetUp(SystemRolePermissionRepository systemRolePermissionRepository, SystemUserRepository systemUserRepository, PasswordEncoder passwordEncoder, SystemDomainRepository domainRepository, SystemPermissionRepository permissionRepository, SystemRoleRepository roleRepository) {
        this.systemRolePermissionRepository = systemRolePermissionRepository;
        this.systemUserRepository = systemUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.domainRepository = domainRepository;
        this.permissionRepository = permissionRepository;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    protected void setupDomains() {
        log.info("Domains supported, setting them up.");

        for (AppDomains domain : AppDomains.values()) {
            // check if a domain exists orElse create it
            log.info("Checking for {} domain", domain.name());

            Optional<SystemDomainModel> existence = domainRepository.findByDomainName(domain.name());
            if (existence.isEmpty()) {
                log.info("{} domain does not exist. Creating it.", domain.name());
                var md = SystemDomainModel.builder();
                md.domainName(String.valueOf(domain));
                domainRepository.save(md.build());
            } else {
                log.info("{} domain already exists.", domain.name());
            }
        }
        log.debug("Domains setup successfully");
    }

    @PostConstruct
    public void setupRoles() {
        DefaultRoles[] values = DefaultRoles.values();
        //add roles to db
        for (DefaultRoles value : values) {
            Optional<SystemRoleModel> existingRole = roleRepository.findByRoleCode(value.name());

            if (existingRole.isEmpty()) {
                SystemRoleModel role = SystemRoleModel.builder()
                        .roleName(value.name().replace("_", " "))
                        .roleCode(value.name())
                        .roleDomain(value.getDomain())
                        .build();
                roleRepository.save(role);
                log.info("Role {} added successfully", role.getRoleName());
                return;
            }

            SystemRoleModel role = existingRole.get();
            if (role.getRoleDomain() == null) {
                role.setRoleDomain(value.getDomain());
                roleRepository.save(role);
            }
        }

        //Setting up role permissions
        setupPermissions();
    }

    public void setupPermissions() {
        DefaultPermissions[] perms = DefaultPermissions.values();

        for (DefaultPermissions perm : perms) {
            log.info("Checking for {} permission", perm.name());
            Optional<SystemPermissionModel> existence = permissionRepository.findByPermissionCode(perm.name());
            if (existence.isEmpty()) {
                SystemPermissionModel permissionsModel = new SystemPermissionModel();
                permissionsModel.setPermissionCode(perm.name());
                permissionsModel.setPermissionName(perm.name().replace("_", " "));

                List<String> domains = perm.getDomain().stream()
                        .map(String::valueOf)
                        .toList();

                permissionsModel.setDomains(domains);
                permissionRepository.save(permissionsModel);
                log.info("{} permission added successfully", perm.name());
            } else {
                log.info("{} permission already exists", perm.name());
            }
        }
        setUpDefaultRolePerms();
    }

    /**
     * By default, the system creates the first role of ADMINISTRATOR; therefore, this method is to assign it its default permissions.
     * This will assign all the permissions that set 'shipWithAdmin' to true.
     */
    private void setUpDefaultRolePerms() {
        DefaultRoles[] roles = DefaultRoles.values();
        for (DefaultRoles role : roles) {
            role.getPermissions().forEach(permission -> {
                log.info("Assigning {} permission to {} role", permission.name(), role.getRoleName());
                List<AppDomains> domains = permission.getDomain();
                domains.forEach(domain -> {
                    if (!Objects.equals(role.getDomain(), domain)) {
                        log.warn("Permission {} does not match role domain {}. Skipping assignment.", permission.name(), role.getDomain());
                        return; // Skip if the permission's domain does not match the role's domain
                    }
                    Optional<SystemRolePermissionAssignmentModel> rolePerms = systemRolePermissionRepository.findByRoleCodeAndPermissionCode(role.name(), permission.name());

                    if (rolePerms.isPresent()) {
                        log.info("Permission {} already assigned to role {}", permission.name(), role.getRoleName());
                        return; // Skip if the permission is already assigned to the role
                    }
                    SystemRolePermissionAssignmentModel assignment = new SystemRolePermissionAssignmentModel();
                    assignment.setRoleCode(role.getCode());
                    assignment.setPermissionCode(permission.name());
                    systemRolePermissionRepository.save(assignment);
                    log.info("{} permission assigned to {} role successfully", permission.name(), role.getRoleName());
                });

            });
        }
        createDefaultUser();
    }

    private void createDefaultUser() {
        SystemUserModel user = new SystemUserModel();
        user.setFirstName("Julian");
        user.setLastName("Sepius");
        user.setEmail("ceres1738@gmail.com");
        user.setRoleCode(DefaultRoles.SUPER_ADMIN.name());
        user.setPassword(passwordEncoder.encode(RAW_ADMIN_PASSWORD)); // This should be hashed in a real application
        user.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        user.setIsActive(true);
        Optional<SystemUserModel> existingUser = systemUserRepository.findByEmail(user.getEmail());
        if (existingUser.isEmpty()) {
            systemUserRepository.save(user);
            log.info("Default user created successfully: {}", user.getEmail());
        } else {
            log.info("Default user already exists: {}", user.getEmail());
        }
    }
}
