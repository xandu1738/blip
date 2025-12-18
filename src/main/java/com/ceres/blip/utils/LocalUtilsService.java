package com.ceres.blip.utils;

import com.ceres.blip.config.ApplicationConf;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.models.database.ModuleModel;
import com.ceres.blip.models.database.PartnerModel;
import com.ceres.blip.models.database.SystemRoleModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.repositories.ModuleRepository;
import com.ceres.blip.repositories.PartnersRepository;
import com.ceres.blip.repositories.SystemRoleRepository;
import com.ceres.blip.repositories.SystemUserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nullable;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public abstract class LocalUtilsService {
    private static final String AUTHENTICATION_REQUIRED = "AUTHENTICATION REQUIRED";
    @Autowired
    private SystemUserRepository userRepository;
    @Autowired
    private SystemRoleRepository roleRepository;
    @Autowired
    private ApplicationConf userDetailService;
    @Autowired
    private PartnersRepository partnersRepository;
    @Autowired
    private ModuleRepository moduleRepository;

    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String DATA = "data";

    /**
     * Works as ```requires()``` above, but will check for only one field
     * This will check for one field at a time
     *
     * @param fields  String - The key to look for
     * @param request JSONObject - The object to check in
     */
    public void requires(JsonNode request, String... fields) {
        for (String field : fields) {
            if (!request.has(field) || request.get(field) == null) {
                throw new IllegalArgumentException(field.replace("_", " ") + " cannot be empty");
            }
        }
    }

    /**
     * If the user is logged in, [has provided the JWT in the Headers], calling this method will return the user
     *
     * @return user details
     */
    public UserDetails getContextUserDetails() {
        return authenticatedUser();
    }

    /**
     * Check if the user is authenticated.
     *
     * @return Boolean - True if the user is authenticated otherwise false
     */
    public Boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return null != authentication
               && authentication.isAuthenticated()
               && !(authentication instanceof AnonymousAuthenticationToken);
    }

    /**
     * This prevents a user from accessing a service if they are not logged in
     */
    public void requiresAuth() throws AuthorizationRequiredException {
        if (Boolean.FALSE.equals(isAuthenticated())) {
            throw new AuthorizationRequiredException(AUTHENTICATION_REQUIRED);
        }
    }

    /**
     * Returns the currently logged-in user.
     *
     * @return SystemUserModel | UserDetails
     */
    public SystemUserModel authenticatedUser() {
        if (Boolean.TRUE.equals(isAuthenticated())) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            return userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                    () -> new IllegalStateException("USER NOT FOUND")
            );
        }
        throw new IllegalArgumentException(AUTHENTICATION_REQUIRED);
    }

    /**
     * Check if the logged-in user has a certain role
     *
     * @param roleCode The code of the role to check for.
     * @return Boolean
     */
    public Boolean hasRole(String roleCode) {
        SystemUserModel usersModel = authenticatedUser();
        if (usersModel != null) {
            Optional<SystemRoleModel> rolesModel = roleRepository.findByRoleCode(usersModel.getRoleCode());
            if (rolesModel.isPresent()) {
                SystemRoleModel role = rolesModel.get();
                return Objects.equals(role.getRoleCode(), roleCode);
            }
        }
        throw new IllegalStateException("USER HAS LESS PRIVILEGES");
    }

    /**
     * This method checks if the logged-in user has a certain permission.
     *
     * @param permission The permission to check for
     * @param username   Optional - If given, it will override using the logged-in user but instead use the user with this username
     * @return Boolean
     */
    public Boolean can(String permission, @Nullable String username) {
        UserDetails userDetails;
        if (username != null) {
            userDetails = userDetailService.loadUserByUsername(username);
        } else {
            userDetails = getContextUserDetails();
        }
        // check the permission in quest
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().matches(permission))) {
            return true;
        }
        throw new IllegalStateException("NOT AUTHORISED");
    }

    /**
     * This performs the same as `can` above but gives you an opportunity to customize the error you want to throw back
     *
     * @param permission Permission Code to check for
     * @param username   Optional - If given, it will override using the logged-in user but instead use the user with this username
     * @param error      String - The error message to use
     */
    public Boolean canWithCustomError(String permission, @Nullable String username, @NonNull String error) {
        UserDetails userDetails;
        if (username != null) {
            userDetails = userDetailService.loadUserByUsername(username);
        } else {
            userDetails = getContextUserDetails();
        }
        // check the permission in quest
        if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().matches(permission))) {
            return true;
        }
        throw new IllegalStateException(error);
    }

    /**
     * Returns all the permissions of the logged-in user
     *
     * @param username Optional - if this is provided, it will override and get the permission of the user with the given username
     * @return List of permissions
     */
    public List<String> userPerms(@Nullable String username) {
        List<String> perms = new ArrayList<>();
        UserDetails userDetails = getContextUserDetails();

        if (username != null) {
            userDetails = userDetailService.loadUserByUsername(username);
        }
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            perms.add(authority.getAuthority());
        }
        return perms;
    }

    /**
     * Checks if the user has any of the listed permissions
     *
     * @param permissions List of permissions
     * @param username    Username of the user to check otherwise, will default to the currently logged-in user.
     * @return Boolean whether user has the permission otherwise throw an exception
     */
    public Boolean can(List<String> permissions, @Nullable String username) {
        UserDetails userDetails = getContextUserDetails();
        if (username != null) {
            userDetails = userDetailService.loadUserByUsername(username);
        }
        // check the permission in quest
        for (String permission : permissions) {
            if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().matches(permission))) {
                return true;
            }
        }
        throw new IllegalStateException("NOT AUTHORISED");
    }

    /**
     * Get the role of the logged-in user
     *
     * @return SystemRoleModel - The role object
     */
    public SystemRoleModel getRole() {
        String roleCode = authenticatedUser().getRoleCode();
        // query for the role code
        Optional<SystemRoleModel> rolesModel = roleRepository.findByRoleCode(roleCode);
        if (rolesModel.isEmpty()) {
            throw new IllegalStateException("UNKNOWN USER ROLE");
        }
        return rolesModel.get();
    }

    /**
     * Checks if the user has access to a certain domain
     *
     * @param domains AppDomains - The domain in quest
     */
    public void belongsTo(AppDomains... domains) {

        for (AppDomains domain : domains) {
            if (getUserDomain() != domain) {
                throw new IllegalStateException("You have no access to the " + domain + " services");
            }
        }

    }

    /**
     * The domain of the currently logged-in user
     * Remember, users don't belong to a domain directly but via the role assigned to them.
     *
     * @return AppDomain -- The domain String of the user according to the role assigned to them.
     * @implNote This may be null if the entire app is not supporting domains
     */
    public AppDomains getUserDomain() {
        return getRole().getRoleDomain();
    }

    public Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }


    public static Timestamp stringToTimestamp(String dateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        try {
            Date parsedDate = dateFormat.parse(dateString);
            return new Timestamp(parsedDate.getTime());
        } catch (ParseException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

    public SystemUserModel getUserById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("User Id cannot be null");
        }

        return userRepository.findById(id).orElseThrow(
                () -> new IllegalStateException("User profile not found")
        );
    }

    public String generateRandomString(Long length) {
        String string = UUID.randomUUID().toString().replaceAll("_", "").toUpperCase();
        if (length == null || length < 1) {
            length = 10L;
        }
        if (string.length() > length.intValue()) {
            return string;
        }
        return string.substring(0, length.intValue());
    }

    public PartnerModel validatePartner(String partnerCode) {
        return partnersRepository.findByPartnerCode(partnerCode).orElseThrow(
                () -> new IllegalStateException(String.format("Partner with code %s not found. Please contact admin to set up.", partnerCode))
        );
    }

    public ModuleModel getModule(String moduleCode) {
        return moduleRepository.findByCode(moduleCode).orElseThrow(
                () -> new IllegalStateException(String.format("Module with code %s not found. Please contact admin.", moduleCode))
        );
    }

    public void executeAsync(Runnable runnable) {
        CompletableFuture.runAsync(runnable);
    }


    protected JsonNode getRequestData(JsonNode request) {
        requires(request, DATA);
        return request.get(DATA);
    }
}

