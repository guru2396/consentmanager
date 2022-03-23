package com.consent.consentmanager.dto;

import lombok.Data;

import java.util.List;

@Data
public class EhrDetails {

    private String ehrId;

    private List<EpisodeDetails> episodeDetails;
}
