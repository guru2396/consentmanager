package com.consent.consentmanager.dto;

import lombok.Data;

import java.util.List;

@Data
public class DataCustodian {

    private String dataCustodianId;

    private List<EpisodeDetails> episodes;
}
