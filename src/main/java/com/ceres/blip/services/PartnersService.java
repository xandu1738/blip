package com.ceres.blip.services;

import com.ceres.blip.dtos.ListResponseDto;
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
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static com.ceres.blip.models.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class PartnersService extends LocalUtilsService {

    private final LocalFileManager localFileManager;
    private final PartnersRepository partnersRepository;


    @CacheEvict(value = "partners", allEntries = true)
    public OperationReturnObject addNewPartner(JsonNode request, HttpServletRequest httpServletRequest) {
        belongsTo(AppDomains.BACK_OFFICE);

        JsonNode data = getRequestData(request);
        requires(data, PARTNER_NAME.name(), ACCOUNT_NUMBER.name(), CONTACT_PERSON.name(), CONTACT_PHONE.name(), ACCOUNT_ID.name(), BUSINESS_REFERENCE.name(), ACTIVE.name(), PACKAGE.name());
        String partnerName = data.get(PARTNER_NAME.name()).asText();

        if (StringUtils.isBlank(partnerName)) {
            throw new IllegalArgumentException("Partner name cannot be empty");
        }
        String logo = data.get("logo").asText();

        String partnerCode = partnerName.toUpperCase().replace(" ", "_");

        Optional<PartnerModel> duplicate = partnersRepository.findByPartnerCode(partnerCode);
        if (duplicate.isPresent()) {
            throw new IllegalArgumentException("Partner with code " + partnerCode + " already exists.");
        }

        PartnerModel partnerModel = new PartnerModel();
        partnerModel.setPartnerName(partnerName);
        partnerModel.setPartnerCode(partnerCode);
        partnerModel.setAccountNumber(data.get(ACCOUNT_NUMBER.name()).asText());
        partnerModel.setContactPerson(data.get(CONTACT_PERSON.name()).asText());
        partnerModel.setContactPhone(data.get(CONTACT_PHONE.name()).asText());
        partnerModel.setAccountId(data.get(ACCOUNT_ID.name()).asLong());
        partnerModel.setBusinessReference(data.get(BUSINESS_REFERENCE.name()).asText());
        partnerModel.setActive(data.get(ACTIVE.name()).asBoolean());

        partnersRepository.flush();

        if (StringUtils.isNotBlank(logo)) {
            if (!logo.startsWith("data:image/")) {
                throw new IllegalArgumentException("Logo must be a base64 encoded image string.");
            }

            try {
                String logoFilePath = localFileManager.handleFileUpload(
                        logo,
                        partnerModel.getId(),
                        FileCategories.PARTNER_LOGO,
                        httpServletRequest);

                partnerModel.setLogo(logoFilePath);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }
        partnerModel.setDateCreated(getCurrentTimestamp());
        String pkg = data.get(PACKAGE.name()).asText();
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
    public OperationReturnObject editPartnerInfo(JsonNode request, HttpServletRequest httpServletRequest) {
        belongsTo(AppDomains.BACK_OFFICE);

        JsonNode data = getRequestData(request);
        Long id = data.get("id").asLong();
        PartnerModel partnerModel = partnersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partner with ID " + id + " not found."));

        String partnerName = data.get(PARTNER_NAME.name()).asText();
        if (StringUtils.isBlank(partnerName)) {
            throw new IllegalArgumentException("Partner name cannot be empty");
        }
        String logo = data.get("logo").asText();
        // Only update fields that are provided in the request
        if (data.has(PARTNER_NAME.name())) {
            partnerModel.setPartnerName(partnerName);
        }
        if (data.has(ACCOUNT_NUMBER.name())) {
            partnerModel.setAccountNumber(data.get(ACCOUNT_NUMBER.name()).asText());
        }
        if (data.has(CONTACT_PERSON.name())) {
            partnerModel.setContactPerson(data.get(CONTACT_PERSON.name()).asText());
        }
        if (data.has(CONTACT_PHONE.name())) {
            partnerModel.setContactPhone(data.get(CONTACT_PHONE.name()).asText());
        }
        if (data.has(ACCOUNT_ID.name())) {
            partnerModel.setAccountId(data.get(ACCOUNT_ID.name()).asLong());
        }
        if (data.has(BUSINESS_REFERENCE.name())) {
            partnerModel.setBusinessReference(data.get(BUSINESS_REFERENCE.name()).asText());
        }

        if (StringUtils.isNotBlank(logo)) {
            if (!logo.startsWith("data:image/")) {
                throw new IllegalArgumentException("Logo must be a base64 encoded image string.");
            }
            try {
                String logoFilePath = localFileManager.handleFileUpload(
                        logo,
                        partnerModel.getId(),
                        FileCategories.PARTNER_LOGO,
                        httpServletRequest);

                partnerModel.setLogo(logoFilePath);
            } catch (IOException e) {
                throw new IllegalStateException(e.getMessage());
            }
        }

        String pkg = data.get(PACKAGE.name()).asText();
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

    @Cacheable(value = "partners", key = "#pageNumber + '-' + #pageSize")
    public OperationReturnObject fetchPartnersList(int pageNumber, int pageSize) throws AuthorizationRequiredException {
        belongsTo(AppDomains.BACK_OFFICE);
        requiresAuth();

        List<PartnerModel> partners = partnersRepository.findAll(PageRequest.of(pageNumber, pageSize))
                .toList();
        //Total count of partners
        Optional<Map<String, Object>> partnersCount = partnersRepository.partnersCount();

        Long count = 10L;
        if (partnersCount.isPresent()) {
            count = (Long) partnersCount.get().get("count");
        }
        ListResponseDto listResponseDto = new ListResponseDto(count, partners);
        return new OperationReturnObject(200, "Partners list successfully fetched.", listResponseDto);
    }

    @CachePut(value = "partners", key = "#request.data.id")
    public OperationReturnObject updatePartnerStatus(JsonNode request) throws AuthorizationRequiredException {
        belongsTo(AppDomains.BACK_OFFICE);
        requiresAuth();
        JsonNode data = getRequestData(request);
        requires(data, "id", ACTIVE.name());

        Long id = data.get("id").asLong();
        Boolean active = data.get(ACTIVE.name()).asBoolean();

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
