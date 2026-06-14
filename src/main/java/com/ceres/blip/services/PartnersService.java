package com.ceres.blip.services;

import com.ceres.blip.annotations.base.RequiresAuthentication;
import com.ceres.blip.dtos.mappers.ResponseDtoMapper;
import com.ceres.blip.models.database.PartnerModel;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.models.enums.BlipPackages;
import com.ceres.blip.models.enums.FileCategories;
import com.ceres.blip.repositories.PartnersRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.utils.LocalFileManager;
import com.ceres.blip.dtos.OperationReturnObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static com.ceres.blip.models.enums.Constants.*;

@Service
@RequiredArgsConstructor
public class PartnersService extends LocalUtilsService {

    private final LocalFileManager localFileManager;
    private final PartnersRepository partnersRepository;
    private final ResponseDtoMapper responseDtoMapper;


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

    @RequiresAuthentication
    @Cacheable(value = "partners", key = "#params['pageNumber'] + '-' + #params['pageSize']")
    public OperationReturnObject fetchPartnersList(Map<String, Object> params) {
        belongsTo(AppDomains.BACK_OFFICE);
        int pageNumber = (int) params.get("pageNumber");
        int pageSize = (int) params.get("pageSize");
        String sortField = (String) params.get("sortField");
//        String search = (String) params.get("search");
        Integer sortOrder = (Integer) params.get("sortOrder");

        if (StringUtils.isNotBlank(sortField)) {
            sortField = "id";
            sortOrder = -1;
        }
        Specification<PartnerModel> spec = null;
//        if (StringUtils.isNotBlank(search)) {
//            ObjectMapper mapper = new ObjectMapper();
//            search = search.trim();
//            try {
//                JsonNode searchObj = mapper.readTree(search);
//                Map<String, Object> map = mapper.readValue(searchObj.toString(), Map.class);
//
//                spec = buildSearchSpec(map);
//            } catch (JsonProcessingException e) {
//                throw new IllegalStateException(e.getMessage());
//            }
//
//        }

        Sort sort = Sort.by(
                sortOrder > 0 ?
                        Sort.Direction.ASC :
                        Sort.Direction.DESC, sortField);

        Page<PartnerModel> partners;
        PageRequest pageable = PageRequest.of(pageNumber, pageSize, sort);
//        if (spec != null) {
//            partners = partnersRepository.findAll(spec, pageable);
//        } else {
            partners = partnersRepository.findAll(pageable);
//        }
        return responseDtoMapper.apply(partners);
    }

    @RequiresAuthentication
    @Cacheable(value = "partnerList")
    public OperationReturnObject filterPartnersList(JsonNode request) {
        belongsTo(AppDomains.BACK_OFFICE);

        Page<PartnerModel> partners = partnersRepository.findByArchivedFalse(Pageable.ofSize(20));
        return responseDtoMapper.apply(partners);
    }

    @RequiresAuthentication
    @CacheEvict(value = "partners", allEntries = true)
    public OperationReturnObject updatePartnerStatus(JsonNode request) {
        belongsTo(AppDomains.BACK_OFFICE);
        JsonNode data = getRequestData(request);
        requires(data, "id", ACTIVE.getValue());

        Long id = data.get("id").asLong();
        Boolean active = data.get(ACTIVE.getValue()).asBoolean();

        PartnerModel partnerModel = partnersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partner with ID " + id + " not found."));

        partnerModel.setActive(active);
        partnerModel.setArchived(false);
        PartnerModel saved = partnersRepository.save(partnerModel);

        String status = active ? "activated" : "deactivated";
        return new OperationReturnObject(200, "Partner successfully " + status + ".", saved);
    }

    @RequiresAuthentication
    @CacheEvict(value = "partners", allEntries = true)
    public OperationReturnObject archivePartner(JsonNode request) {
        belongsTo(AppDomains.BACK_OFFICE);
        JsonNode data = getRequestData(request);
        requires(data, "id");

        Long id = data.get("id").asLong();

        PartnerModel partnerModel = partnersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partner with ID " + id + " not found."));

        partnerModel.setArchived(true);
        partnerModel.setActive(false);
        PartnerModel saved = partnersRepository.save(partnerModel);

        return new OperationReturnObject(200, "Partner successfully archived.", saved);
    }

    @RequiresAuthentication
    @Cacheable(value = "partner", key = "#partnerCode")
    public OperationReturnObject partnerProfile(String partnerCode) {

        if (StringUtils.isBlank(partnerCode)) {
            throw new IllegalArgumentException("Partner code cannot be empty");
        }

        PartnerModel partnerModel = partnersRepository.findByPartnerCode(partnerCode)
                .orElseThrow(() -> new IllegalArgumentException("Partner with code " + partnerCode + " not found."));
        return new OperationReturnObject(200, "Partner profile successfully fetched.", partnerModel);
    }
}
