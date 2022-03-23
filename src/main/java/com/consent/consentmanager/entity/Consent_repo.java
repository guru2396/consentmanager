package com.consent.consentmanager.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="consent_repo")
@Data
public class Consent_repo {

    @Id
    private String consent_id;

    private String patient_id;

    private String doctor_id;

    private String consent_artifact;

    private String is_revoked;

    private Date created_dt;
}
