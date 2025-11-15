package com.ceres.blip.api;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.services.ModulesService;
import com.ceres.blip.utils.OperationReturnObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/modules")
@Tag(name = "Modules", description = "Modules API")
@RequiredArgsConstructor
public class ModuleController {
    private final ModulesService modulesService;

    @GetMapping("/list/{pageNumber}/{pageSize}")
    public ResponseEntity<OperationReturnObject> modulesList(@PathVariable int pageSize, @PathVariable int pageNumber) {
        return ResponseEntity.ok(modulesService.modulesList(pageNumber, pageSize));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OperationReturnObject> moduleDetail(@PathVariable Long id) {
        return ResponseEntity.ok(modulesService.moduleDetail(id));
    }

    @PostMapping("/add-module")
    public ResponseEntity<OperationReturnObject> addModule(@RequestBody JSONObject request) {
        return ResponseEntity.ok(modulesService.addModule(request));
    }

    @PostMapping("/edit-module")
    public ResponseEntity<OperationReturnObject> editModule(@RequestBody JSONObject request) throws AuthorizationRequiredException {
        return ResponseEntity.ok(modulesService.editModule(request));
    }

    @PostMapping("/subscribe")
    public ResponseEntity<OperationReturnObject> subscribeModule(@RequestBody JSONObject request) {
        return ResponseEntity.ok(modulesService.subscribeToModule(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationReturnObject> deleteModule(@PathVariable Long id) {
        return ResponseEntity.ok(modulesService.moduleDetail(id));
    }
}
