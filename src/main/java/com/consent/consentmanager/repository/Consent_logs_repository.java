package com.consent.consentmanager.repository;

import com.consent.consentmanager.entity.Consent_logs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Consent_logs_repository extends JpaRepository<Consent_logs,String> {
}
