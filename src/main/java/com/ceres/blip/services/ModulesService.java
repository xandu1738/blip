package com.ceres.blip.services;

import com.ceres.blip.exceptions.AuthorizationRequiredException;
import com.ceres.blip.models.database.ModuleModel;
import com.ceres.blip.models.database.PartnerModel;
import com.ceres.blip.models.database.SubscriptionModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.models.enums.SubscriptionStatus;
import com.ceres.blip.repositories.ModuleRepository;
import com.ceres.blip.repositories.SubscriptionRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.dtos.OperationReturnObject;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModulesService extends LocalUtilsService{
    private final ModuleRepository repository;
    private final SubscriptionRepository subscriptionRepository;

    public OperationReturnObject addModule(JsonNode object) {
        belongsTo(AppDomains.BACK_OFFICE);
        SystemUserModel authenticatedUser = authenticatedUser();
        requires(object, "data");
        JsonNode data = getRequestData(object);
        requires(data, "name", "description");

        String name = data.get("name").asText();

        if (StringUtils.isBlank(name)) {
            throw new IllegalArgumentException("Module name cannot be empty");
        }

        String code = name.toUpperCase().replaceAll(" ", "_");
        String description = data.get("description").asText();
        if (StringUtils.isBlank(description)) {
            throw new IllegalArgumentException("Module description cannot be empty");
        }

        ModuleModel moduleModel = new ModuleModel();
        moduleModel.setName(name);
        moduleModel.setCode(code);
        moduleModel.setDescription(description);
        moduleModel.setCreatedBy(authenticatedUser.getId());
        moduleModel.setCreatedAt(getCurrentTimestamp());

        repository.save(moduleModel);
        return new OperationReturnObject(200, "Module successfully added.", moduleModel);
    }

    public OperationReturnObject editModule(JsonNode request) throws AuthorizationRequiredException {
        belongsTo(AppDomains.BACK_OFFICE);
        requiresAuth();
        requires(request, "data");
        JsonNode data = getRequestData(request);
        requires(data, "code");
        String code = data.get("code").asText();

        if (StringUtils.isBlank(code)) {
            throw new IllegalArgumentException("Module code cannot be empty");
        }

        ModuleModel moduleModel = repository.findByCode(code).orElseThrow(() -> new IllegalArgumentException("Module with code " + code + " not found."));
        String name = data.get("name").asText();
        String description = data.get("description").asText();

        if (StringUtils.isNotBlank(name)) {
            moduleModel.setName(name);
        }

        if (StringUtils.isNotBlank(description)) {
            moduleModel.setDescription(description);
        }

        repository.save(moduleModel);
        return new OperationReturnObject(200, "Module successfully edited.", moduleModel);
    }

    public OperationReturnObject modulesList(int pageNumber, int pageSize) {
        Page<ModuleModel> modules = repository.findAll(PageRequest.of(pageNumber, pageSize));
        return new OperationReturnObject(200, "Modules list successfully fetched.", modules);
    }

    public OperationReturnObject moduleDetail(Long id) {
        return new OperationReturnObject(200, "Module details successfully fetched.", repository.findById(id).orElse(null));
    }

    public OperationReturnObject removeModule(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Module ID cannot be null");
        }

        Optional<ModuleModel> module = repository.findById(id);
        if (module.isEmpty()) {
            throw new IllegalArgumentException("Module with ID " + id + " not found.");
        }

        repository.deleteById(id);
        return new OperationReturnObject(200, "Module successfully removed.", null);
    }

    public OperationReturnObject subscribeToModule(JsonNode request) {
        SystemUserModel authenticatedUser = authenticatedUser();
        requires(request, "data");
        JsonNode data = getRequestData(request);
        requires(data, "partner_code", "module_code");
        String partnerCode = data.get("partner_code").asText();
        String moduleCode = data.get("module_code").asText();
        if (StringUtils.isBlank(partnerCode) || StringUtils.isBlank(moduleCode)) {
            throw new IllegalArgumentException("Partner code and module code cannot be empty");
        }

        String sd = data.get("start_date").asText();
        String ed = data.get("end_date").asText();

        if(sd == null || ed == null){
            throw new IllegalArgumentException("Start date and end date cannot be empty");
        }

        //String to timestamp
        Timestamp startDate = Timestamp.valueOf(sd);
        Timestamp endDate = Timestamp.valueOf(ed);

        PartnerModel partner = validatePartner(partnerCode);
        ModuleModel module = getModule(moduleCode);

        SubscriptionModel subscription = new SubscriptionModel();
        subscription.setPartnerCode(partner.getPartnerCode());
        subscription.setModuleCode(module.getCode());
        subscription.setCreatedAt(getCurrentTimestamp());
        subscription.setCreatedBy(authenticatedUser.getId());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        //TODO: Link Subscription to its payment and save iff payment is successful.
        subscriptionRepository.save(subscription);

        return new OperationReturnObject(200, "Module successfully subscribed.", null);
    }

    public OperationReturnObject subscriptionsList(String partnerCode, int pageNumber, int pageSize) {
        Optional<Map<String, Object>> subscriptions = subscriptionRepository.getSubscriptions(partnerCode,pageNumber,pageSize);
        return new OperationReturnObject(200, "Subscriptions list successfully fetched.", subscriptions);
    }
}
