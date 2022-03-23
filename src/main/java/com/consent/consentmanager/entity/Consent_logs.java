package com.consent.consentmanager.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name="consent_logs")
@Data
public class Consent_logs {

    @Id
    private String consent_log_id;

    private String consent_id;

    private String patient_id;

    private String operation;

    private String performed_by_id;

    private Date created_dt;

}
