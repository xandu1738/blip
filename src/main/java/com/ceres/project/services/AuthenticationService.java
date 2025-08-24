package com.ceres.project.services;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.project.config.ApplicationConf;
import com.ceres.project.config.JwtUtility;
import com.ceres.project.dtos.UserDto;
import com.ceres.project.dtos.UserDtoMapper;
import com.ceres.project.exceptions.AuthorizationRequiredException;
import com.ceres.project.models.database.PartnerModel;
import com.ceres.project.models.database.SystemRolePermissionAssignmentModel;
import com.ceres.project.models.database.SystemUserModel;
import com.ceres.project.models.jpa_helpers.enums.DefaultRoles;
import com.ceres.project.repositories.SystemRolePermissionRepository;
import com.ceres.project.repositories.SystemUserRepository;
import com.ceres.project.services.base.BaseWebActionsService;
import com.ceres.project.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@RequiredArgsConstructor
@Slf4j
@Service
public class AuthenticationService extends BaseWebActionsService {
    private final AuthenticationManager authenticationManager;
    private final ApplicationConf userDetailService;
    private final JwtUtility jwtUtility;
    private final UserDtoMapper userDtoMapper;
    private final SystemRolePermissionRepository systemRolePermissionRepository;
    private final SystemUserRepository systemUserRepository;
    private final PasswordEncoder passwordEncoder;


    private OperationReturnObject login(JSONObject request) {
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");

        requires(data, "email", "password");

        String email = data.getString("email");
        String password = data.getString("password");

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
        response.put("accessToken", accessToken); // this is the jwt token the user can user from now on.
        response.put("refreshToken", refreshToken); // this is the jwt token the user can user from now on.
        response.put("user", profile);
        response.put("permissions", permission);
        return new OperationReturnObject(200, "Welcome back " + userDetails.getUsername(), response);
    }

    private OperationReturnObject refreshToken(JSONObject request) throws AuthorizationRequiredException {
        requiresAuth();
        requires(request, "data");

        JSONObject data = request.getJSONObject("data");
        requires(data, "refreshToken");

        try {
            String refreshToken = data.getString("refreshToken");
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

    private OperationReturnObject signUp(JSONObject request) {
        SystemUserModel authenticatedUser = authenticatedUser();
        requires(request, "data");
        JSONObject data = request.getJSONObject("data");
        requires(data, "role", "first_name", "last_name", "email", "password");
        String role = data.getString("role");
        String firstName = data.getString("first_name");
        String lastName = data.getString("last_name");
        String email = data.getString("email");
        String password = data.getString("password");

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

        return new OperationReturnObject(200, "User created successfully", null);
    }

    private OperationReturnObject usersList(JSONObject request) throws AuthorizationRequiredException {
        requiresAuth();
        JSONObject search = request.getJSONObject("search");

        if (search == null) {
            search = new JSONObject();
        }

        List<UserDto> users = systemUserRepository.findAll().stream().map(userDtoMapper).toList();

        return new OperationReturnObject(200, "Users list successfully", users);
    }

    private OperationReturnObject usersProfile(JSONObject request) throws AuthorizationRequiredException {
        requiresAuth();
        JSONObject search = request.getJSONObject("search");

        if (search == null) {
            search = new JSONObject();
        }

        Long id = search.getLong("id");
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


    @Override
    public OperationReturnObject switchActions(String action, JSONObject request) throws AuthorizationRequiredException {
        return switch (action) {
            case "login" -> login(request);
            case "refreshToken" -> refreshToken(request);
            case "signUp" -> signUp(request);
            case "usersList" -> usersList(request);
            case "usersProfile" -> usersProfile(request);

            default -> throw new IllegalArgumentException("Action " + action + " not known in this context");
        };
    }
}
