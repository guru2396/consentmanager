package com.consent.consentmanager.service;

import com.consent.consentmanager.dto.*;
import com.consent.consentmanager.entity.Consent_repo;
import com.consent.consentmanager.repository.Consent_repository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ConsentManagerService {

    @Autowired
    private Consent_repository consent_repository;

    public List<GrantedConsentResponse> getGrantedConsents(String doctorId){
        List<Consent_repo> consentList=consent_repository.getGrantedConsentsForDoctor(doctorId);
        List<GrantedConsentResponse> respList=new ArrayList<>();
        ObjectMapper mapper=new ObjectMapper();
        for(Consent_repo consent:consentList){
            Consent_Artifact artifact=null;
            try {
                artifact=mapper.readValue(consent.getConsent_artifact(),Consent_Artifact.class);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            if(artifact!=null){
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date validity=null;
                try {
                    validity=dateFormat.parse(artifact.getValidityDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if(new Date().before(validity)){
                    GrantedConsentResponse grantedConsentResponse=new GrantedConsentResponse();
                    grantedConsentResponse.setConsent_id(consent.getConsent_id());
                    grantedConsentResponse.setPatient_id(consent.getPatient_id());
                    //grantedConsentResponse.setDelegateAccess(consent.get);

                    grantedConsentResponse.setDelegateAccess(artifact.getDelegateAccess());
                    grantedConsentResponse.setValidity(artifact.getValidityDate());
                    respList.add(grantedConsentResponse);
                }
            }
        }
        return respList;
    }

    public String createConsent(CreateConsent createConsent){
        Consent_repo consent_repo=new Consent_repo();
        consent_repo.setPatient_id(createConsent.getPatient_id());
        consent_repo.setDoctor_id(createConsent.getDoctor_id());
        consent_repo.setIs_revoked("N");
        consent_repo.setCreated_dt(new Date());
        Consent_Artifact artifact=new Consent_Artifact();
        artifact.setPatientId(createConsent.getPatient_id());
        artifact.setBeneficiaryId(createConsent.getDoctor_id());
        artifact.setDataCustodianId(createConsent.getDataCustodianId());
        artifact.setPurpose(createConsent.getPurpose());
        artifact.setDelegateAccess(createConsent.getDelegateAccess());
        artifact.setSignature(createConsent.getSignature());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date=dateFormat.format(new Date());
        artifact.setCreationDate(date);
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE,90);
        String validity=dateFormat.format(calendar.getTime());
        artifact.setValidityDate(validity);
        artifact.setSignature("signature");
        EhrDetails ehrDetails=new EhrDetails();
        ehrDetails.setEhrId(createConsent.getEhr_id());
        List<EpisodeDetails> episodeDetails=new ArrayList<>();
        if(createConsent.getEpisodes()!=null){
            for(EpisodeDetails episode:createConsent.getEpisodes()){
                EpisodeDetails details=new EpisodeDetails();
                details.setEpisodeId(episode.getEpisodeId());
                details.setTime_limit_records(episode.getTime_limit_records());
                List<EncounterDetails> encounterDetails=new ArrayList<>();
                if(episode.getEncounterDetails()!=null){
                    for(EncounterDetails encounter:episode.getEncounterDetails()){
                        EncounterDetails ecDetails=new EncounterDetails();
                        ecDetails.setEncounterId(encounter.getEncounterId());
                        encounterDetails.add(ecDetails);
                    }
                }
                details.setEncounterDetails(encounterDetails);
                episodeDetails.add(details);
            }
        }
        ehrDetails.setEpisodeDetails(episodeDetails);
        artifact.setEhrDetails(ehrDetails);
        ObjectMapper mapper=new ObjectMapper();
        String artifactJson=null;
        try {
            artifactJson= mapper.writeValueAsString(artifact);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
        consent_repo.setConsent_artifact(artifactJson);
        long id=generateID();
        String consentId="Cons_" + id;
        consent_repo.setConsent_id(consentId);
        try{
            consent_repository.save(consent_repo);
        }catch(Exception e){
            System.out.println(e);
            return null;
        }
        return consent_repo.getConsent_id();

    }

    public ValidateConsentResponse validateConsent(String consentId){
        Consent_repo consent=consent_repository.getConsentById(consentId);
        String artifactJson=consent.getConsent_artifact();
        ObjectMapper mapper=new ObjectMapper();
        Consent_Artifact artifact=null;
        try {
            artifact=mapper.readValue(artifactJson,Consent_Artifact.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        ValidateConsentResponse validateConsentResponse=null;
        if(artifact!=null){
            validateConsentResponse=new ValidateConsentResponse();
            validateConsentResponse.setDataCustodianId(artifact.getDataCustodianId());
            validateConsentResponse.setEpisodes(artifact.getEhrDetails().getEpisodeDetails());
        }
        return validateConsentResponse;

    }

    public List<ConsentDto> retrieveConsents(String patientId){
        List<ConsentDto> consentDtoList=new ArrayList<>();
        List<Consent_repo> consent_repoList= consent_repository.getConsentsByPatientId(patientId);
        if(consent_repoList!=null){
            ObjectMapper mapper=new ObjectMapper();
            for(Consent_repo consent_repo:consent_repoList){
                ConsentDto consentDto=new ConsentDto();
                consentDto.setConsent_id(consent_repo.getConsent_id());
                consentDto.setDoctor_id(consent_repo.getDoctor_id());
                DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                String date=dateFormat.format(consent_repo.getCreated_dt());
                consentDto.setCreation_date(date);
                Consent_Artifact artifact=null;
                try {
                    artifact=mapper.readValue(consent_repo.getConsent_artifact(),Consent_Artifact.class);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
                consentDto.setDataCustodianId(artifact.getDataCustodianId());
                consentDto.setAccess_purpose(artifact.getPurpose());
                consentDto.setDelegate_access(artifact.getDelegateAccess());
                consentDto.setValidity(artifact.getValidityDate());
                consentDtoList.add(consentDto);
            }
        }
        return consentDtoList;
    }

    public String delegateConsent(String doctorId,String consentId){
        Consent_repo consent_repo= consent_repository.getConsentById(consentId);
        Consent_repo delegatedConsent=new Consent_repo();
        delegatedConsent.setDoctor_id(doctorId);
        delegatedConsent.setPatient_id(consent_repo.getPatient_id());
        delegatedConsent.setCreated_dt(new Date());
        delegatedConsent.setIs_revoked("N");
        ObjectMapper objectMapper=new ObjectMapper();
        Consent_Artifact artifact=null;
        try {
            artifact=objectMapper.readValue(consent_repo.getConsent_artifact(),Consent_Artifact.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Calendar calendar=Calendar.getInstance();
        calendar.add(Calendar.DATE,1);
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String date=dateFormat.format(calendar.getTime());
        String creat_date=dateFormat.format(new Date());
        artifact.setValidityDate(date);
        artifact.setBeneficiaryId(doctorId);
        artifact.setCreationDate(creat_date);
        String newArtifact=null;
        try {
            newArtifact=objectMapper.writeValueAsString(artifact);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        delegatedConsent.setConsent_artifact(newArtifact);
        long id=generateID();
        String newConsentId="Cons_" + id;
        delegatedConsent.setConsent_id(newConsentId);
        consent_repository.save(delegatedConsent);
        return consent_repo.getPatient_id();
    }

    public long generateID(){
        long id=(long) Math.floor(Math.random()*9_000_000_000L)+1_000_000_000L;
        return id;
    }

    public String revokeConsent(String consentId){
        Consent_repo consent_repo= consent_repository.getConsentById(consentId);
        if(consent_repo!=null){
            consent_repo.setIs_revoked("Y");
            consent_repository.save(consent_repo);
            return "Success";
        }
        return null;
    }


}
