package com.ceres.blip.services;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.models.database.PartnerModel;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.models.enums.BlipPackages;
import com.ceres.blip.models.enums.FileCategories;
import com.ceres.blip.models.enums.Params;
import com.ceres.blip.repositories.PartnersRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.utils.LocalFileManager;
import com.ceres.blip.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class PartnersService extends LocalUtilsService {
    private final LocalFileManager localFileManager;
    private final PartnersRepository partnersRepository;


    public OperationReturnObject addNewPartner(JSONObject request) {
        belongsTo(AppDomains.BACK_OFFICE);
        requires(request, Params.DATA.getRef());

        JSONObject data = request.getJSONObject(Params.DATA.getRef());
        requires(data,
                Params.PARTNER_NAME.getRef(), Params.ACCOUNT_NUMBER.getRef(), Params.CONTACT_PERSON.getRef(),
                Params.CONTACT_PHONE.getRef(), Params.ACCOUNT_ID.getRef(), Params.BUSINESS_REFERENCE.getRef(),
                Params.ACCOUNT_NUMBER.getRef(), Params.PACKAGE.getRef());

        String partnerName = data.getString(Params.PARTNER_NAME.getRef());

        if (StringUtils.isBlank(partnerName)) {
            throw new IllegalArgumentException("Partner name cannot be empty");
        }
        String logo = data.getString("logo");

        String partnerCode = partnerName.toUpperCase().replace(" ", "_");
        PartnerModel partnerModel = new PartnerModel();
        partnerModel.setPartnerName(partnerName);
        partnerModel.setPartnerCode(partnerCode);
        partnerModel.setAccountNumber(data.getString(Params.ACCOUNT_NUMBER.getRef()));
        partnerModel.setContactPerson(data.getString(Params.CONTACT_PERSON.getRef()));
        partnerModel.setContactPhone(data.getString(Params.CONTACT_PHONE.getRef()));
        partnerModel.setAccountId(data.getLong(Params.ACCOUNT_ID.getRef()));
        partnerModel.setBusinessReference(data.getString(Params.BUSINESS_REFERENCE.getRef()));
        partnerModel.setActive(data.getBoolean(Params.ACCOUNT_NUMBER.getRef()));

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
        String pkg = data.getString(Params.PACKAGE.getRef());
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
    public OperationReturnObject editPartnerInfo(JSONObject request) {
        belongsTo(AppDomains.BACK_OFFICE);
        requires(request, Params.DATA.getRef());

        JSONObject data = request.getJSONObject(Params.DATA.getRef());
        Long id = data.getLong(Params.ID.getRef());
        PartnerModel partnerModel = partnersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(String.format("Partner with ID %d not found.", id)));

        String partnerName = data.getString(Params.PARTNER_NAME.getRef());
        if (StringUtils.isBlank(partnerName)) {
            throw new IllegalArgumentException("Partner name cannot be empty");
        }
        String logo = data.getString("logo");
        // Only update fields that are provided in the request
        if (data.containsKey(Params.PARTNER_NAME.getRef())) {
            partnerModel.setPartnerName(partnerName);
        }
        if (data.containsKey(Params.ACCOUNT_NUMBER.getRef())) {
            partnerModel.setAccountNumber(data.getString(Params.ACCOUNT_NUMBER.getRef()));
        }
        if (data.containsKey(Params.CONTACT_PERSON.getRef())) {
            partnerModel.setContactPerson(data.getString(Params.CONTACT_PERSON.getRef()));
        }
        if (data.containsKey(Params.CONTACT_PHONE.getRef())) {
            partnerModel.setContactPhone(data.getString(Params.CONTACT_PHONE.getRef()));
        }
        if (data.containsKey(Params.ACCOUNT_ID.getRef())) {
            partnerModel.setAccountId(data.getLong(Params.ACCOUNT_ID.getRef()));
        }
        if (data.containsKey(Params.BUSINESS_REFERENCE.getRef())) {
            partnerModel.setBusinessReference(data.getString(Params.BUSINESS_REFERENCE.getRef()));
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

        String pkg = data.getString(Params.PACKAGE.getRef());
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
    public OperationReturnObject fetchPartnersList(int pageNumber, int pageSize) {
//        belongsTo(AppDomains.BACK_OFFICE);
//        requiresAuth();

        Page<PartnerModel> partners = partnersRepository.findAll(PageRequest.of(pageNumber, pageSize));

        return new OperationReturnObject(200, "Partners list successfully fetched.", partners);
    }

    @CachePut(value = "partners", key = "#request.data.id")
    public OperationReturnObject updatePartnerStatus(JSONObject request) throws AuthorizationRequiredException {
        belongsTo(AppDomains.BACK_OFFICE);
        requiresAuth();
        requires(request, Params.DATA.getRef());
        JSONObject data = request.getJSONObject(Params.DATA.getRef());
        requires(data, Params.ID.getRef(), Params.ACCOUNT_NUMBER.getRef());

        Long id = data.getLong(Params.ID.getRef());
        Boolean active = data.getBoolean(Params.ACCOUNT_NUMBER.getRef());

        PartnerModel partnerModel = partnersRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Partner with ID " + id + " not found."));

        partnerModel.setActive(active);
        PartnerModel saved = partnersRepository.save(partnerModel);

        String status = Boolean.TRUE.equals(active) ? "activated" : "deactivated";
        return new OperationReturnObject(200, "Partner successfully " + status + ".", saved);
    }

    @Cacheable(value = "partner", key = "#partnerCode")
    public OperationReturnObject partnerProfile(String partnerCode) throws AuthorizationRequiredException {
//        requiresAuth();

        if (StringUtils.isBlank(partnerCode)) {
            throw new IllegalArgumentException("Partner code cannot be empty");
        }

        PartnerModel partnerModel = partnersRepository.findByPartnerCode(partnerCode)
                .orElseThrow(() -> new IllegalArgumentException("Partner with code " + partnerCode + " not found."));
        return new OperationReturnObject(200, "Partner profile successfully fetched.", partnerModel);
    }
}
