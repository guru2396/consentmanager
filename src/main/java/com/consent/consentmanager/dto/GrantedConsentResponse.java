package com.consent.consentmanager.dto;

import lombok.Data;

@Data
public class GrantedConsentResponse {

    private String patient_id;

    private String consent_id;

    private String delegateAccess;

    private String validity;
}
