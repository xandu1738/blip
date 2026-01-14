package com.ceres.blip.services;

import com.ceres.blip.models.database.*;
import com.ceres.blip.models.enums.AppDomains;
import com.ceres.blip.models.enums.Constants;
import com.ceres.blip.models.enums.SubscriptionRequestStatus;
import com.ceres.blip.repositories.SubscriptionPlanRepository;
import com.ceres.blip.repositories.SubscriptionRequestRepository;
import com.ceres.blip.repositories.SubscriptionsRepository;
import com.ceres.blip.utils.LocalUtilsService;
import com.ceres.blip.dtos.OperationReturnObject;
import com.ceres.blip.utils.mail.MessagingService;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionService extends LocalUtilsService {
    private final SubscriptionPlanRepository subscriptionPlanRepository;
    private final SubscriptionsRepository subscriptionsRepository;
    private final SubscriptionRequestRepository subscriptionRequestRepository;
    private final MessagingService messagingService;


    @Value("${contacts.phone.mtn:0777914904}")
    private String mtnContact;

    @Value("${contacts.phone.airtel:0708998491}")
    private String airtelContact;


    @CacheEvict(value = "subscription-plans", allEntries = true)
    public OperationReturnObject saveSubscriptionPlan(JsonNode request) {
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

    @CacheEvict(value = "subscription-requests", allEntries = true)
    public OperationReturnObject saveSubscriptionRequest(JsonNode request) {
        SystemUserModel authenticatedUser = authenticatedUser();
        JsonNode data = getRequestData(request);

        requires(data, Constants.SUBSCRIPTION_ID.getValue());
        PartnerModel partnerModel = validatePartner(authenticatedUser.getPartnerCode());

        Long subscription = data.get(Constants.SUBSCRIPTION_ID.getValue()).asLong();
        var updateSubscriptionNode = data.get(Constants.UPDATE_SUBSCRIPTION.getValue());
        boolean updateSubscription = false;
        if (updateSubscriptionNode != null){
            updateSubscription = updateSubscriptionNode.asBoolean();
        }

        Optional<SubscriptionRequestModel> existingPending = subscriptionRequestRepository.findByPartnerCodeAndStatus(partnerModel.getPartnerCode(), SubscriptionRequestStatus.PAYMENT_PENDING);
        if (existingPending.isPresent()) {
            if (updateSubscription) {
                existingPending.get().setSubscriptionPlan(subscription);
                existingPending.get().setRequestedOn(getCurrentTimestamp());

                subscriptionRequestRepository.save(existingPending.get());
                return new OperationReturnObject(200, "Subscription Request Updated Successfully", null);
            }
            throw new IllegalStateException("You have an existing pending subscription request. Would you like to update?");
        }

        SubscriptionRequestModel subscriptionRequest = new SubscriptionRequestModel();
        subscriptionRequest.setPartnerCode(partnerModel.getPartnerCode());
        subscriptionRequest.setSubscriptionPlan(subscription);
        subscriptionRequest.setStatus(SubscriptionRequestStatus.PAYMENT_PENDING);
        subscriptionRequest.setUserId(authenticatedUser().getId());
        subscriptionRequest.setRequestedOn(getCurrentTimestamp());

        String reference = generateSubscriptionRequest(partnerModel.getPartnerCode());
        subscriptionRequest.setSubscriptionReference(reference);
        subscriptionRequestRepository.save(subscriptionRequest);

        String mailBody = """
                <p>Dear %s %s,</p>
                <p>Your Subscription is currently being processed:</p>
                <p>Send the subscription fee to either of the accounts</p>
                <ul>
                    <li><strong>MTN:</strong> %s</li>
                    <li><strong>Airtel:</strong> %s</li>
                </ul>
                <p>Use the reference <strong>%s</strong> as reason/narration for the transaction.</p>
                <p>In case the subscription delays to effect, please contact us.</p>
                <p>Best regards,<br/>
                The Blip Team</p>
                """.formatted(authenticatedUser.getFirstName(), authenticatedUser.getLastName(), mtnContact, airtelContact, reference);

        messagingService.sendTemplateMail(authenticatedUser, "Account Created Successfully", mailBody, "email-template");

        return new OperationReturnObject(200, "Subscription Plan successfully added.", null);
    }

    private String generateSubscriptionRequest(String partnerCode) {
        if (StringUtils.isBlank(partnerCode)) {
            throw new IllegalStateException("Please provide partner code");
        }

        String reference = partnerCode.substring(0, 3).concat(getCurrentTimestamp().toString());
        if (reference.length() < 14) {
            reference = reference.concat(getCurrentTimestamp().toString());
        }

        if (reference.length() > 14) {
            reference = reference.substring(0, 13);
        }
        return reference;
    }

    @Cacheable(value = "subscription-plans", key = "#pageNumber+#pageSize")
    public OperationReturnObject getSubscriptionPlans(int pageNumber, int pageSize) {
        List<SubscriptionPlanModel> plans = subscriptionPlanRepository.findAll(PageRequest.of(pageNumber, pageSize))
                .toList();
        return new OperationReturnObject(200, null, plans);
    }

    @Cacheable(value = "subscription-requests", key = "#pageNumber+#pageSize")
    public OperationReturnObject getSubscriptionRequests(int pageNumber, int pageSize) {
        List<SubscriptionRequestModel> planRequests = subscriptionRequestRepository.findAll(PageRequest.of(pageNumber, pageSize))
                .toList();
        return new OperationReturnObject(200, null, planRequests);
    }

    @CacheEvict(value = "subscriptions", allEntries = true)
    public OperationReturnObject saveSubscription(JsonNode request) {
        requires(request, Constants.DATA.getValue());
        JsonNode data = getRequestData(request);
        return null;
    }


    @Cacheable(value = "subscriptions", key = "#pageNumber+#pageSize")
    public OperationReturnObject getSubscriptions(int pageNumber, int pageSize) {
        List<PartnerSubscriptionModel> subscriptions = subscriptionsRepository.findAll(PageRequest.of(pageNumber, pageSize))
                .toList();
        return new OperationReturnObject(200, null, subscriptions);
    }
}
