package com.ceres.blip.api;

import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.services.PartnersService;
import com.ceres.blip.utils.OperationReturnObject;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<OperationReturnObject> addNewPartner(@RequestBody JsonNode request, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(partnersService.addNewPartner(request, httpServletRequest));
    }

    @PostMapping("/edit-partner-info")
    public ResponseEntity<OperationReturnObject> editPartnerInfo(@RequestBody JsonNode request, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(partnersService.editPartnerInfo(request, httpServletRequest));
    }

    @PostMapping("/update-partner-status")
    public ResponseEntity<OperationReturnObject> updatePartnerStatus(@RequestBody JsonNode request) throws AuthorizationRequiredException {
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
