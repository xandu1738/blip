package com.ceres.blip.services;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.models.database.PartnerModel;
import com.ceres.blip.models.jpa_helpers.enums.AppDomains;
import com.ceres.blip.models.jpa_helpers.enums.BlipPackages;
import com.ceres.blip.models.jpa_helpers.enums.FileCategories;
import com.ceres.blip.repositories.PartnersRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.utils.LocalFileManager;
import com.ceres.blip.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnersService {
    private final LocalFileManager localFileManager;
    private final PartnersRepository partnersRepository;
    private final LocalUtilsService localUtilsService;


    private OperationReturnObject addNewPartner(JSONObject request) {
        localUtilsService.belongsTo(AppDomains.BACK_OFFICE);
        localUtilsService.requires(request, "data");

        JSONObject data = request.getJSONObject("data");
        localUtilsService.requires(data, "partner_name", "account_number", "contact_person", "contact_phone", "account_id", "business_reference", "active", "logo", "package");
        String partnerName = data.getString("partner_name");

        if(StringUtils.isBlank(partnerName)){
            throw new IllegalArgumentException("Partner name cannot be empty");
        }
        String logo = data.getString("logo");

        String partnerCode = partnerName.toUpperCase().replaceAll(" ", "_");
        PartnerModel partnerModel = new PartnerModel();
        partnerModel.setPartnerName(partnerName);
        partnerModel.setPartnerCode(partnerCode);
        partnerModel.setAccountNumber(data.getString("account_number"));
        partnerModel.setContactPerson(data.getString("contact_person"));
        partnerModel.setContactPhone(data.getString("contact_phone"));
        partnerModel.setAccountId(data.getLong("account_id"));
        partnerModel.setBusinessReference(data.getString("business_reference"));
        partnerModel.setActive(data.getBoolean("active"));

        if (StringUtils.isNotBlank(logo)){
            if (!logo.startsWith("data:image/")) {
                throw new IllegalArgumentException("Logo must be a base64 encoded image string.");
            }
            try {
                String logoFilePath = localFileManager.storeBase64File(logo, localUtilsService.generateRandomString(20L), FileCategories.PARTNER_LOGO);
                partnerModel.setLogo(logoFilePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        partnerModel.setDateCreated(localUtilsService.getCurrentTimestamp());
        String pkg = data.getString("package");
        if(StringUtils.isBlank(pkg)){
            pkg = "FULL";
        }

        if (!EnumUtils.isValidEnumIgnoreCase(BlipPackages.class, pkg)) {
            throw new IllegalArgumentException("Invalid package value. Allowed values are: TRANSPORT, LOGISTICS or FULL.");
        }
        partnerModel.setPackageField(pkg);
        PartnerModel saved = partnersRepository.save(partnerModel);

        return new OperationReturnObject(200, "Partner successfully added.",saved);

    }

    private OperationReturnObject editPartnerInfo(JSONObject request) {
        localUtilsService.belongsTo(AppDomains.BACK_OFFICE);
        localUtilsService.requires(request, "data");

        JSONObject data = request.getJSONObject("data");
        Long id = data.getLong("id");
        PartnerModel partnerModel = partnersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partner with ID " + id + " not found."));

        String partnerName = data.getString("partner_name");
        if (StringUtils.isBlank(partnerName)) {
            throw new IllegalArgumentException("Partner name cannot be empty");
        }
        String logo = data.getString("logo");
        // Only update fields that are provided in the request
        if (data.containsKey("partner_name")) {
            partnerModel.setPartnerName(partnerName);
        }
        if (data.containsKey("account_number")) {
            partnerModel.setAccountNumber(data.getString("account_number"));
        }
        if (data.containsKey("contact_person")) {
            partnerModel.setContactPerson(data.getString("contact_person"));
        }
        if (data.containsKey("contact_phone")) {
            partnerModel.setContactPhone(data.getString("contact_phone"));
        }
        if (data.containsKey("account_id")) {
            partnerModel.setAccountId(data.getLong("account_id"));
        }
        if (data.containsKey("business_reference")) {
            partnerModel.setBusinessReference(data.getString("business_reference"));
        }

        if (StringUtils.isNotBlank(logo)) {
            if (!logo.startsWith("data:image/")) {
                throw new IllegalArgumentException("Logo must be a base64 encoded image string.");
            }
            try {
                String logoFilePath = localFileManager.storeBase64File(logo, localUtilsService.generateRandomString(20L), FileCategories.PARTNER_LOGO);
                partnerModel.setLogo(logoFilePath);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        String pkg = data.getString("package");
        if (StringUtils.isNotBlank(pkg)) {
            partnerModel.setPackageField(pkg);
        }

        if (!EnumUtils.isValidEnumIgnoreCase(BlipPackages.class, pkg)) {
            throw new IllegalArgumentException("Invalid package value. Allowed values are: TRANSPORT, LOGISTICS or FULL.");
        }
        partnerModel.setPackageField(pkg);
        PartnerModel saved = partnersRepository.save(partnerModel);

        return new OperationReturnObject(200, "Partner info successfully updated.", saved);
    }

    private OperationReturnObject fetchPartnersList(JSONObject request) throws AuthorizationRequiredException {
        localUtilsService.belongsTo(AppDomains.BACK_OFFICE);
        localUtilsService.requiresAuth();
        localUtilsService.requires(request, "search");
        JSONObject search = request.getJSONObject("search");
        if (search == null) {
            search = new JSONObject();
        }
        Integer pageNumber = search.getInteger("page");
        if (pageNumber == null || pageNumber < 1) {
            pageNumber = 1;
        }
        Integer pageSize = search.getInteger("page_size");
        if (pageSize == null || pageSize < 1) {
            pageSize = 10;
        }

        List<PartnerModel> partners = partnersRepository.findAll(PageRequest.of(pageNumber,pageSize)).toList();
        return new OperationReturnObject(200, "Partners list successfully fetched.", partners);
    }

    private OperationReturnObject updatePartnerStatus(JSONObject request) throws AuthorizationRequiredException {
        localUtilsService.belongsTo(AppDomains.BACK_OFFICE);
        localUtilsService.requiresAuth();
        localUtilsService.requires(request, "data");
        JSONObject data = request.getJSONObject("data");
        localUtilsService.requires(data, "id", "active");

        Long id = data.getLong("id");
        Boolean active = data.getBoolean("active");

        PartnerModel partnerModel = partnersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partner with ID " + id + " not found."));

        partnerModel.setActive(active);
        PartnerModel saved = partnersRepository.save(partnerModel);

        String status = active ? "activated" : "deactivated";
        return new OperationReturnObject(200, "Partner successfully " + status + ".", saved);
    }
}
