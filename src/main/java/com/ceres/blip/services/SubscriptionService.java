package com.ceres.blip.services;

import com.ceres.blip.models.database.PartnerSubscriptionModel;
import com.ceres.blip.models.database.SubscriptionPlanModel;
import com.ceres.blip.models.database.SystemUserModel;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.models.enums.Constants;
import com.ceres.blip.repositories.SubscriptionPlanRepository;
import com.ceres.blip.repositories.SubscriptionsRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.utils.OperationReturnObject;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionService extends LocalUtilsService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionsRepository subscriptionsRepository;

    private OperationReturnObject saveSubscriptionPlan(JsonNode request) {
        belongsTo(AppDomains.BACK_OFFICE);
        SystemUserModel authenticatedUser = authenticatedUser();

        JsonNode data = getRequestData(request);

        requires(data, Constants.NAME.getValue(), Constants.DESCRIPTION.getValue());
        String name = data.get(Constants.NAME.getValue()).asText();
        String description = data.get(Constants.DESCRIPTION.getValue()).asText();

        if (StringUtils.isBlank(name) || StringUtils.isBlank(description)) {
            throw new IllegalStateException("Plan name and description can not be blank.");
        }

        SubscriptionPlanModel plan = new SubscriptionPlanModel();
        plan.setName(name);
        plan.setDescription(description);
        plan.setCreatedBy(authenticatedUser.getId());

        subscriptionPlanRepository.save(plan);
        return new OperationReturnObject(200, "Subscription Plan successfully added.", null);
    }

    private OperationReturnObject getSubscriptionPlans(JsonNode request) {
        List<SubscriptionPlanModel> plans = subscriptionPlanRepository.findAll();
        return new OperationReturnObject(200, null, plans);
    }

    private OperationReturnObject saveSubscription(JsonNode request) {
        requires(request, Constants.DATA.getValue());
        JsonNode data = getRequestData(request);
        return null;
    }

    private OperationReturnObject getSubscriptions(JsonNode request) {
        List<PartnerSubscriptionModel> subscriptions = subscriptionsRepository.findAll();
        return new OperationReturnObject(200, null, subscriptions);
    }
}
