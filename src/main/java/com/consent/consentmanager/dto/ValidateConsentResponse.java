package com.consent.consentmanager.dto;

import lombok.Data;

import java.util.List;

@Data
public class ValidateConsentResponse {

    private String dataCustodianId;

    private String accessPurpose;

    private List<EpisodeDetails> episodes;
}
