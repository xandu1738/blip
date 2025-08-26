package com.ceres.blip.api;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.services.PartnersService;
import com.ceres.blip.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/partners")
@RequiredArgsConstructor
public class PartnersController {
    private final PartnersService partnersService;

    @PostMapping("/add-partner")
    public OperationReturnObject addNewPartner(@RequestBody JSONObject request) {
        return partnersService.addNewPartner(request);
    }

    @PostMapping("/edit-partner-info")
    public OperationReturnObject editPartnerInfo(@RequestBody JSONObject request) {
        return partnersService.editPartnerInfo(request);
    }

    @PostMapping("/update-partner-status")
    public OperationReturnObject updatePartnerStatus(@RequestBody JSONObject request) throws AuthorizationRequiredException {
        return partnersService.updatePartnerStatus(request);
    }

    @GetMapping("/list/{pageNumber}/{pageSize}")
    public OperationReturnObject fetchPartnersList(@PathVariable int pageNumber, @PathVariable int pageSize) throws AuthorizationRequiredException {
        return partnersService.fetchPartnersList(pageNumber, pageSize);
    }

    @GetMapping("/profile/{partnerCode}")
    public OperationReturnObject partnerProfile(@PathVariable String partnerCode) throws AuthorizationRequiredException {
        return partnersService.partnerProfile(partnerCode);
    }
}
