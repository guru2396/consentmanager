package com.consent.consentmanager.repository;

import com.consent.consentmanager.entity.Consent_repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Consent_repository extends JpaRepository<Consent_repo,String> {

    @Query(value = "SELECT * FROM consent_repo WHERE doctor_id=?1 AND is_revoked='N'",nativeQuery = true)
    List<Consent_repo> getGrantedConsentsForDoctor(String doctorId);

    @Query(value = "SELECT * FROM consent_repo WHERE consent_id=?1",nativeQuery = true)
    Consent_repo getConsentById(String consentId);

    @Query(value = "SELECT * FROM consent_repo WHERE patient_id=?1 AND is_revoked='N'",nativeQuery = true)
    List<Consent_repo> getConsentsByPatientId(String patientId);
}
