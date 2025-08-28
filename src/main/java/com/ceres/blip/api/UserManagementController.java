package com.ceres.blip.api;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.services.UserManagementService;
import com.ceres.blip.utils.OperationReturnObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user-management")
@Tag(name = "User Management", description = "User Management API")
public class UserManagementController {
    private final UserManagementService userManagementService;

    @PostMapping("/create-user")
    public ResponseEntity<OperationReturnObject> createUserProfile(@RequestBody JSONObject request) {
        return ResponseEntity.ok(userManagementService.signUp(request));
    }

    @PostMapping("/login")
    public ResponseEntity<OperationReturnObject> login(@RequestBody JSONObject request) {
        return ResponseEntity.ok(userManagementService.login(request));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<OperationReturnObject> refreshToken(@RequestBody JSONObject request) throws AuthorizationRequiredException {
        return ResponseEntity.ok(userManagementService.refreshToken(request));
    }

    @GetMapping("/users-list/{pageNumber}/{pageSize}")
    public ResponseEntity<OperationReturnObject> usersList(@PathVariable int pageNumber, @PathVariable int pageSize) throws AuthorizationRequiredException {
        return ResponseEntity.ok(userManagementService.usersList(pageNumber, pageSize));
    }

    @GetMapping("/users-profile/{id}")
    public ResponseEntity<OperationReturnObject> usersProfile(@PathVariable long id) throws AuthorizationRequiredException {
        return ResponseEntity.ok(userManagementService.usersProfile(id));
    }
}
