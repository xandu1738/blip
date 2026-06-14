package com.ceres.blip.api;

import com.ceres.blip.services.PartnersService;
import com.ceres.blip.dtos.OperationReturnObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
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
    public ResponseEntity<OperationReturnObject> updatePartnerStatus(@RequestBody JsonNode request) {
        return ResponseEntity.ok(partnersService.updatePartnerStatus(request));
    }

    @PostMapping("/archive-partner")
    public ResponseEntity<OperationReturnObject> archivePartner(@RequestBody JsonNode request) {
        return ResponseEntity.ok(partnersService.archivePartner(request));
    }

    @GetMapping("/list")
    public ResponseEntity<OperationReturnObject> fetchPartnersList(
            @RequestParam int pageNumber,
            @RequestParam int pageSize,
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer sortOrder
    ) {
        Map<String,Object> params = new HashMap<>();
        params.put("sortField", sortField);
        params.put("sortOrder", sortOrder);
        params.put("pageNumber", pageNumber);
        params.put("pageSize", pageSize);
        params.put("search", search);

        return ResponseEntity.ok(partnersService.fetchPartnersList(params));
    }

    @PostMapping("/list")
    public ResponseEntity<OperationReturnObject> filterPartnersList(@RequestBody JsonNode request) {
        return ResponseEntity.ok(partnersService.filterPartnersList(request));
    }

    @GetMapping("/profile/{partnerCode}")
    public ResponseEntity<OperationReturnObject> partnerProfile(@PathVariable String partnerCode) {
        return ResponseEntity.ok(partnersService.partnerProfile(partnerCode));
    }
}
