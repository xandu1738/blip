package com.ceres.blip.api;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.services.ModulesService;
import com.ceres.blip.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/modules")
@RequiredArgsConstructor
public class ModuleController {
    private final ModulesService modulesService;

    @GetMapping("/list/{pageNumber}/{pageSize}")
    public OperationReturnObject modulesList(@PathVariable int pageSize, @PathVariable int pageNumber) {
        return modulesService.modulesList(pageNumber, pageSize);
    }

    @GetMapping("/{id}")
    public OperationReturnObject moduleDetail(@PathVariable Long id) {
        return modulesService.moduleDetail(id);
    }

    @PostMapping("/add-module")
    public OperationReturnObject addModule(@RequestBody JSONObject request){
        return modulesService.addModule(request);
    }

    @PostMapping("/edit-module")
    public OperationReturnObject editModule(@RequestBody JSONObject request) throws AuthorizationRequiredException {
        return modulesService.editModule(request);
    }

    @PostMapping("/subscribe")
    public OperationReturnObject subscribeModule(@RequestBody JSONObject request) {
        return modulesService.subscribeToModule(request);
    }

    @DeleteMapping("/{id}")
    public OperationReturnObject deleteModule(@PathVariable Long id){
        return modulesService.moduleDetail(id);
    }
}
