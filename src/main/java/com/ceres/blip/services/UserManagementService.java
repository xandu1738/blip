package com.ceres.blip.services;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.config.ApplicationConf;
import com.ceres.blip.config.JwtUtility;
import com.ceres.blip.dtos.UserDto;
import com.ceres.blip.dtos.UserDtoMapper;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.models.database.PartnerModel;
import com.ceres.blip.models.database.SystemRolePermissionAssignmentModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.models.jpa_helpers.enums.DefaultRoles;
import com.ceres.blip.repositories.SystemRolePermissionRepository;
import com.ceres.blip.repositories.SystemUserRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.utils.OperationReturnObject;
import com.ceres.blip.utils.events.UserRegistrationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManagementService extends LocalUtilsService {
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final String USER_EMAIL = "email";
    private static final String USER_PASSWORD = "password";

    @Value("${application.url}")
    private String applicationUrl;

    private final AuthenticationManager authenticationManager;
    private final ApplicationConf userDetailService;
    private final JwtUtility jwtUtility;
    private final UserDtoMapper userDtoMapper;
    private final SystemRolePermissionRepository systemRolePermissionRepository;
    private final SystemUserRepository systemUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher publisher;


    public OperationReturnObject login(JSONObject request) {
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");

        requires(data, USER_EMAIL, USER_PASSWORD);

        String email = data.getString(USER_EMAIL);
        String password = data.getString(USER_PASSWORD);

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
        } catch (AuthenticationException e) {
            return new OperationReturnObject(401, "Invalid email or password", null);
        }

        final SystemUserModel userDetails = userDetailService.loadUserByUsername(email);
        final String accessToken = jwtUtility.generateAccessToken(userDetails, "ACCESS");
        final String refreshToken = jwtUtility.generateAccessToken(userDetails, "REFRESH");

        UserDto profile = userDtoMapper.apply(userDetails);

        //For consistency with other user-querying calls
        List<String> permission = systemRolePermissionRepository.findAllByRoleCode(userDetails.getRoleCode())
                .stream()
                .map(SystemRolePermissionAssignmentModel::getPermissionCode)
                .toList();

        Map<String, Object> response = new HashMap<>();
        response.put(ACCESS_TOKEN, accessToken); // this is the jwt token the user can user from now on.
        response.put(REFRESH_TOKEN, refreshToken); // this is the jwt token the user can user from now on.
        response.put("user", profile);
        response.put("permissions", permission);
        return new OperationReturnObject(200, "Welcome back " + userDetails.getUsername(), response);
    }

    public OperationReturnObject refreshToken(JSONObject request) throws AuthorizationRequiredException {
        requiresAuth();
        requires(request, "data");

        JSONObject data = request.getJSONObject("data");
        requires(data, REFRESH_TOKEN);

        try {
            String refreshToken = data.getString(REFRESH_TOKEN);
            String tokenType = jwtUtility.extractTokenType(refreshToken);
            log.info(tokenType);
            if (!Objects.equals(tokenType, "REFRESH"))
                throw new IllegalArgumentException("Refresh Token Required. You have provided an access token.");

            if (jwtUtility.isTokenValid(refreshToken, getContextUserDetails())) {
                String token = jwtUtility.generateAccessToken(getContextUserDetails(), "ACCESS");
                return new OperationReturnObject(200, "Token successfully Refreshed.", token);
            }
            return new OperationReturnObject(401, "Invalid Refresh Token", null);
        } catch (Exception e) {
            e.printStackTrace();
            return new OperationReturnObject(500, e.getMessage(), null);
        }
    }

    public OperationReturnObject signUp(JSONObject request) {
        SystemUserModel authenticatedUser = authenticatedUser();
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");
        requires(data, "role", "first_name", "last_name", USER_EMAIL, USER_PASSWORD);
        String role = data.getString("role");
        String firstName = data.getString("first_name");
        String lastName = data.getString("last_name");
        String email = data.getString(USER_EMAIL);
        String password = data.getString(USER_PASSWORD);

        SystemUserModel user = new SystemUserModel();
        if (!Objects.equals(role, DefaultRoles.SUPER_ADMIN.name()) || !Objects.equals(authenticatedUser.getRoleCode(), DefaultRoles.SUPER_ADMIN.name())) {
            requires(data, "partner");
            String partner = data.getString("partner");
            PartnerModel partnerProfile = validatePartner(partner);
            user.setPartnerCode(partnerProfile.getPartnerCode());
        }

        if (StringUtils.isBlank(firstName) || StringUtils.isBlank(lastName)) {
            throw new IllegalArgumentException("First name and last name are required");
        }

        if (StringUtils.isBlank(email)) {
            throw new IllegalArgumentException("Email is required");
        }

        if (StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Password is required");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setRoleCode(role);
        user.setPassword(passwordEncoder.encode(password));
        user.setCreatedAt(getCurrentTimestamp());
        user.setIsActive(true);

        systemUserRepository.save(user);

        executeAsync(() -> {
            log.info("Sending user registration event for user {}", user.getEmail());
            publisher.publishEvent(new UserRegistrationEvent(user, applicationUrl, password));
        });

        return new OperationReturnObject(200, "User created successfully", null);
    }

    @Cacheable(value = "users", key = "#pageNumber + '-' + #pageSize")
    public OperationReturnObject usersList(int pageNumber, int pageSize) throws AuthorizationRequiredException {
        requiresAuth();

        List<UserDto> users = systemUserRepository.findAll(PageRequest.of(pageNumber, pageSize))
                .stream()
                .map(userDtoMapper)
                .toList();

        return new OperationReturnObject(200, "Users list successfully", users);
    }

    @Cacheable(value = "user", key = "#id")
    public OperationReturnObject usersProfile(Long id) throws AuthorizationRequiredException {
        requiresAuth();
        if (id == null) {
            throw new IllegalStateException("Please specify user's ID");
        }

        var user = systemUserRepository.findById(id)
                .orElseThrow(
                        () -> new IllegalFormatFlagsException("User profile does not exist")
                );

        UserDto userDto = userDtoMapper.apply(user);

        return new OperationReturnObject(200, "Users list successfully", userDto);
    }
}
