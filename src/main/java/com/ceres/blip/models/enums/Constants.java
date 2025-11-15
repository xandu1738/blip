package com.ceres.blip.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Constants {
    SEARCH_FIELD("searchField"),
    SEARCH_VALUE("searchValue"),
    SORT_FIELD("sortField"),
    ORDER("order"),
    ADDRESS("address"),
    PAGE("page"),
    SIZE("size"),
    TOTAL_RECORDS("totalRecords"),
    RECORDS("records"),
    DATA("data"),
    ADDED_BY("addedBy"),
    USER_ID("userId"),
    CONTACT_NUMBER("contactNumber"),
    ID("id"),
    USERNAME("username"),
    PASSWORD("password"),
    FULL_NAME("full_name"),
    EMAIL("email"),
    PHONE("phone"),
    ACCOUNT_TYPE("accountType"),
    ACCESS_TOKEN("accessToken"),
    REFRESH_TOKEN("refreshToken"),
    USER_DATA("userData"),
    FIRST_NAME("firstName"),
    LAST_NAME("lastName"),
    COMPANY_PHONE("companyPhone"),
    COMPANY_NAME("companyName"),
    NAME("name"),
    DESCRIPTION("description"),
    USER_TYPE("userType"),
    MEMBER_SINCE("memberSince"),
    PROFILE_IMAGE("profileImage"),
    VERIFIED("verified"),
    BIO("bio"),
    LOCATION("location"),
    RATING("rating"),
    SEARCH("search"),
    TYPE("type"),
    STATUS("status"),
    PRICE("price"),
    PARTNER_CODE("partnerCode"),
    MODULE_CODE("moduleCode"),
    SUBSCRIPTION_PLAN("subscriptionPlan"),
    START_DATE("startDate"),
    END_DATE("endDate"),
    AMENITIES("amenities"),
    REVIEW_COUNT("reviewCount");
    private final String value;
}
