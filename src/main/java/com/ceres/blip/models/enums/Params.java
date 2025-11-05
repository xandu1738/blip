package com.ceres.blip.models.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Params {
    ID("id"),
    NAME("name"),
    EMAIL("email"),
    CODE("code"),
    DATA("data"),
    DESCRIPTION("description"),
    PARTNER_NAME("partner_name"),
    PARTNER_CODE("partner_code"),
    MODULE_NAME("module_name"),
    MODULE_CODE("module_code"),
    START_DATE("start_date"),
    END_DATE("end_date"),
    ACCOUNT_NUMBER("account_number"),
    CONTACT_PERSON("contact_person"),
    CONTACT_PHONE("contact_phone"),
    ACCOUNT_ID("account_id"),
    BUSINESS_REFERENCE("business_reference"),
    ACTIVE("active"),
    PACKAGE("package");;

    private final String ref;
}
