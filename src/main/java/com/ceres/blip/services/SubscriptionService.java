package com.ceres.blip.services;

import com.alibaba.fastjson2.JSONObject;
import com.ceres.blip.models.database.*;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.models.enums.BlipModules;
import com.ceres.blip.models.enums.Constants;
import com.ceres.blip.models.enums.SubscriptionStatus;
import com.ceres.blip.repositories.SubscriptionPlanRepository;
import com.ceres.blip.repositories.SubscriptionsRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.utils.OperationReturnObject;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionService extends LocalUtilsService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionsRepository subscriptionsRepository;

    public OperationReturnObject saveSubscriptionPlan(JSONObject request) {
        belongsTo(AppDomains.BACK_OFFICE);
        SystemUserModel authenticatedUser = authenticatedUser();

        requires(request, Constants.DATA.getValue());
        JSONObject data = request.getJSONObject(Constants.DATA.getValue());

        requires(data, Constants.NAME.getValue(), Constants.DESCRIPTION.getValue());
        String name = data.getString(Constants.NAME.getValue());
        String description = data.getString(Constants.DESCRIPTION.getValue());

        if (StringUtils.isBlank(name) || StringUtils.isBlank(description)) {
            throw new IllegalStateException("Plan name and description can not be blank.");
        }
        String planCode = name.toUpperCase().replace("-", "_").replace("_", "");
        Optional<SubscriptionPlanModel> existingCode = subscriptionPlanRepository.findByPlanCode(planCode);
        if (existingCode.isPresent()) {
            throw new IllegalStateException("A subscription plan with the same name already exists.");
        }

        SubscriptionPlanModel plan = new SubscriptionPlanModel();
        plan.setName(name);
        plan.setPlanCode(planCode);
        plan.setDescription(description);
        plan.setCreatedBy(authenticatedUser.getId());

        subscriptionPlanRepository.save(plan);
        return new OperationReturnObject(200, "Subscription Plan successfully added.", null);
    }

    public OperationReturnObject getSubscriptionPlans(int pageNumber, int pageSize) {
        Page<SubscriptionPlanModel> plans = subscriptionPlanRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return new OperationReturnObject(200, null, plans);
    }

    public OperationReturnObject saveSubscription(JSONObject request) {
        SystemUserModel authenticatedUser = authenticatedUser();
        requires(request, Constants.DATA.getValue());
        JSONObject data = request.getJSONObject(Constants.DATA.getValue());

        requires(data, Constants.PARTNER_CODE.getValue(), Constants.MODULE_CODE.getValue(),
                Constants.SUBSCRIPTION_PLAN.getValue(), Constants.START_DATE.getValue(),
                Constants.END_DATE.getValue());

        String start = data.getString(Constants.START_DATE.getValue());
        String end = data.getString(Constants.END_DATE.getValue());
        String plan = data.getString(Constants.SUBSCRIPTION_PLAN.getValue());

        PartnerModel partner = validatePartner(data.getString(Constants.PARTNER_CODE.getValue()));
        SubscriptionPlanModel subscriptionPlan = getSubscriptionPlan(plan);

        SubscriptionModel subscription = new SubscriptionModel();
        subscription.setPartnerCode(partner.getPartnerCode());
        subscription.setModuleCode(BlipModules.valueOf(data.getString(Constants.MODULE_CODE.getValue())).name());
        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setSubscriptionPlan(subscriptionPlan.getPlanCode());
        subscription.setStartDate(stringToTImestamp(start));
        subscription.setEndDate(stringToTImestamp(end));
        subscription.setCreatedBy(authenticatedUser.getId());
        subscriptionsRepository.save(subscription);
        return new OperationReturnObject(200, "Subscription successfully created.", null);
    }

    public OperationReturnObject getSubscriptions(int pageNumber, int pageSize) {
        Page<SubscriptionModel> subscriptions = subscriptionsRepository.findAll(PageRequest.of(pageNumber, pageSize));
        return new OperationReturnObject(200, null, subscriptions);
    }

    public OperationReturnObject getSubscriptionPlanDetails(String planCode) {
        SubscriptionPlanModel subscriptionPlan = getSubscriptionPlan(planCode);
        return new OperationReturnObject(200, null, subscriptionPlan);
    }

    public OperationReturnObject getSubscriptionDetails(Long id) {
        SubscriptionModel subscription = subscriptionsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Subscription with ID " + id + " not found."));
        return new OperationReturnObject(200, null, subscription);
    }
}
