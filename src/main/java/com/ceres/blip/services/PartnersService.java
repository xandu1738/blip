package com.ceres.blip.services;

import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.models.database.PartnerModel;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.models.enums.BlipPackages;
import com.ceres.blip.models.enums.FileCategories;
import com.ceres.blip.repositories.PartnersRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.utils.LocalFileManager;
import com.ceres.blip.utils.OperationReturnObject;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PartnersService extends LocalUtilsService {
    private final LocalFileManager localFileManager;
    private final PartnersRepository partnersRepository;


    public OperationReturnObject addNewPartner(JsonNode request) {
        belongsTo(AppDomains.BACK_OFFICE);

        JsonNode data = getRequestData(request);
        requires(data, "partner_name", "account_number", "contact_person", "contact_phone", "account_id", "business_reference", "active", "package");
        String partnerName = data.get("partner_name").asText();

        if (StringUtils.isBlank(partnerName)) {
            throw new IllegalArgumentException("Partner name cannot be empty");
        }
        String logo = data.get("logo").asText();

        String partnerCode = partnerName.toUpperCase().replace(" ", "_");
        PartnerModel partnerModel = new PartnerModel();
        partnerModel.setPartnerName(partnerName);
        partnerModel.setPartnerCode(partnerCode);
        partnerModel.setAccountNumber(data.get("account_number").asText());
        partnerModel.setContactPerson(data.get("contact_person").asText());
        partnerModel.setContactPhone(data.get("contact_phone").asText());
        partnerModel.setAccountId(data.get("account_id").asLong());
        partnerModel.setBusinessReference(data.get("business_reference").asText());
        partnerModel.setActive(data.get("active").asBoolean());

        if (StringUtils.isNotBlank(logo)) {
            if (!logo.startsWith("data:image/")) {
                throw new IllegalArgumentException("Logo must be a base64 encoded image string.");
            }

            String encodedString = logo.substring(logo.indexOf(',') + 1);
            try {
                String logoFilePath = localFileManager.storeBase64File(encodedString, generateRandomString(20L), FileCategories.PARTNER_LOGO);
                partnerModel.setLogo(logoFilePath);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        partnerModel.setDateCreated(getCurrentTimestamp());
        String pkg = data.get("package").asText();
        if (StringUtils.isBlank(pkg)) {
            pkg = "FULL";
        }

        if (!EnumUtils.isValidEnumIgnoreCase(BlipPackages.class, pkg)) {
            throw new IllegalArgumentException("Invalid package value. Allowed values are: TRANSPORT, LOGISTICS or FULL.");
        }
        partnerModel.setPackageField(pkg);
        PartnerModel saved = partnersRepository.save(partnerModel);

        return new OperationReturnObject(200, "Partner successfully added.", saved);

    }

    @CachePut(value = "partners", key = "#request.data.id")
    public OperationReturnObject editPartnerInfo(JsonNode request) {
        belongsTo(AppDomains.BACK_OFFICE);

        JsonNode data = getRequestData(request);
        Long id = data.get("id").asLong();
        PartnerModel partnerModel = partnersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partner with ID " + id + " not found."));

        String partnerName = data.get("partner_name").asText();
        if (StringUtils.isBlank(partnerName)) {
            throw new IllegalArgumentException("Partner name cannot be empty");
        }
        String logo = data.get("logo").asText();
        // Only update fields that are provided in the request
        if (data.has("partner_name")) {
            partnerModel.setPartnerName(partnerName);
        }
        if (data.has("account_number")) {
            partnerModel.setAccountNumber(data.get("account_number").asText());
        }
        if (data.has("contact_person")) {
            partnerModel.setContactPerson(data.get("contact_person").asText());
        }
        if (data.has("contact_phone")) {
            partnerModel.setContactPhone(data.get("contact_phone").asText());
        }
        if (data.has("account_id")) {
            partnerModel.setAccountId(data.get("account_id").asLong());
        }
        if (data.has("business_reference")) {
            partnerModel.setBusinessReference(data.get("business_reference").asText());
        }

        if (StringUtils.isNotBlank(logo)) {
            if (!logo.startsWith("data:image/")) {
                throw new IllegalArgumentException("Logo must be a base64 encoded image string.");
            }
            try {
                String logoFilePath = localFileManager.storeBase64File(logo, generateRandomString(20L), FileCategories.PARTNER_LOGO);
                partnerModel.setLogo(logoFilePath);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }

        String pkg = data.get("package").asText();
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

//    @Cacheable(value = "partners", key = "#pageNumber + '-' + #pageSize")
    public OperationReturnObject fetchPartnersList(int pageNumber, int pageSize) throws AuthorizationRequiredException {
//        belongsTo(AppDomains.BACK_OFFICE);
//        requiresAuth();

        List<PartnerModel> partners = partnersRepository.findAll(PageRequest.of(pageNumber, pageSize)).toList();

        return new OperationReturnObject(200, "Partners list successfully fetched.", partners);
    }

    @CachePut(value = "partners", key = "#request.data.id")
    public OperationReturnObject updatePartnerStatus(JsonNode request) throws AuthorizationRequiredException {
        belongsTo(AppDomains.BACK_OFFICE);
        requiresAuth();
        JsonNode data = getRequestData(request);
        requires(data, "id", "active");

        Long id = data.get("id").asLong();
        Boolean active = data.get("active").asBoolean();

        PartnerModel partnerModel = partnersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partner with ID " + id + " not found."));

        partnerModel.setActive(active);
        PartnerModel saved = partnersRepository.save(partnerModel);

        String status = active ? "activated" : "deactivated";
        return new OperationReturnObject(200, "Partner successfully " + status + ".", saved);
    }

    @Cacheable(value = "partner", key = "#partnerCode")
    public OperationReturnObject partnerProfile(String partnerCode) throws AuthorizationRequiredException {
        requiresAuth();

        if (StringUtils.isBlank(partnerCode)) {
            throw new IllegalArgumentException("Partner code cannot be empty");
        }

        PartnerModel partnerModel = partnersRepository.findByPartnerCode(partnerCode)
                .orElseThrow(() -> new IllegalArgumentException("Partner with code " + partnerCode + " not found."));
        return new OperationReturnObject(200, "Partner profile successfully fetched.", partnerModel);
    }
}
