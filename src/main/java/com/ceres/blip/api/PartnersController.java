package com.ceres.blip.api;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.services.PartnersService;
import com.ceres.blip.utils.OperationReturnObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/partners")
@RequiredArgsConstructor
@Tag(name = "Partners", description = "Partners API")
public class PartnersController {
    private final PartnersService partnersService;

    @PostMapping("/add-partner")
    public ResponseEntity<OperationReturnObject> addNewPartner(@RequestBody JSONObject request) {
        return ResponseEntity.ok(partnersService.addNewPartner(request));
    }

    @PostMapping("/edit-partner-info")
    public ResponseEntity<OperationReturnObject> editPartnerInfo(@RequestBody JSONObject request) {
        return ResponseEntity.ok(partnersService.editPartnerInfo(request));
    }

    @PostMapping("/update-partner-status")
    public ResponseEntity<OperationReturnObject> updatePartnerStatus(@RequestBody JSONObject request) throws AuthorizationRequiredException {
        return ResponseEntity.ok(partnersService.updatePartnerStatus(request));
    }

    @GetMapping("/list/{pageNumber}/{pageSize}")
    public ResponseEntity<OperationReturnObject> fetchPartnersList(@PathVariable int pageNumber, @PathVariable int pageSize) throws AuthorizationRequiredException {
        return ResponseEntity.ok(partnersService.fetchPartnersList(pageNumber, pageSize));
    }

    @GetMapping("/profile/{partnerCode}")
    public ResponseEntity<OperationReturnObject> partnerProfile(@PathVariable String partnerCode) throws AuthorizationRequiredException {
        return ResponseEntity.ok(partnersService.partnerProfile(partnerCode));
    }
}
