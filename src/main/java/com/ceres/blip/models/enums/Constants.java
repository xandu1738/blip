package com.ceres.blip.models.enums;

import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Constants {
    SEARCH_FIELD("searchField"),
    SEARCH_VALUE("searchValue"),
    SORT_FIELD("sortField"),
    ORDER("order"),
    AMOUNT("amount"),
    CONFIRM("confirm"),
    ADDRESS("address"),
    PAGE("page"),
    SIZE("size"),
    TOTAL_RECORDS("totalRecords"),
    RECORDS("records"),
    DATA("data"),
    ADDED_BY("addedBy"),
    USER_ID("partnerCode"),
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
    AMENITIES("amenities"),
    REVIEW_COUNT("reviewCount"),
    SUBSCRIPTION_ID("subscription_id"),
    UPDATE_SUBSCRIPTION("update_subscription"),
    SUBSCRIPTION_PERIOD("subscription_period"),
    SUBSCRIPTION_REFERENCE("subscription_reference"),

    PARTNER_NAME("partner_name"),
    PARTNER_CODE("partner_code"),
    ACCOUNT_NUMBER("account_number"),
    CONTACT_PERSON("contact_person"),
    CONTACT_PHONE("contact_phone"),
    ACCOUNT_ID("account_id"),
    BUSINESS_REFERENCE("business_reference"),
    ACTIVE("active"),
    USER_NOT_FOUND("User not found"),
    PACKAGE("package");

    private final String value;
}
