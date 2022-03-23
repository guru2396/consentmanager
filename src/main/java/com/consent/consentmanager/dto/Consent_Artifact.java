package com.consent.consentmanager.dto;

import lombok.Data;

@Data
public class Consent_Artifact {
    private String patientId;

    private String beneficiaryId;

    private String dataCustodianId;

    private EhrDetails ehrDetails;

    private String purpose;

    private String delegateAccess;

    private String creationDate;

    private String validityDate;

    private String signature;

    private String systemId;
}
