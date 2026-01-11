package com.ceres.blip.api;

import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.services.ModulesService;
import com.ceres.blip.utils.OperationReturnObject;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Modules")
@RestController
@RequestMapping("/api/v1/modules")
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
    public ResponseEntity<OperationReturnObject> addModule(@RequestBody JsonNode request) {
        return ResponseEntity.ok(modulesService.addModule(request));
    }

    @PostMapping("/edit-module")
    public ResponseEntity<OperationReturnObject> editModule(@RequestBody JsonNode request) throws AuthorizationRequiredException {
        return ResponseEntity.ok(modulesService.editModule(request));
    }

    @PostMapping("/subscribe")
    public ResponseEntity<OperationReturnObject> subscribeModule(@RequestBody JsonNode request) {
        return ResponseEntity.ok(modulesService.subscribeToModule(request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<OperationReturnObject> deleteModule(@PathVariable Long id) {
        return ResponseEntity.ok(modulesService.moduleDetail(id));
    }
}
