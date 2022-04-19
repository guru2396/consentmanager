package com.consent.consentmanager.controller;

import com.consent.consentmanager.dto.*;
import com.consent.consentmanager.service.ConsentManagerService;
import com.consent.consentmanager.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ConsentManagerController {

    @Autowired
    private ConsentManagerService consentManagerService;

    @Autowired
    private JwtService jwtService;

    @Value("${hospital.client.id}")
    private String hospitalClientId;

    @Value("${hospital.client.secret}")
    private String hospitalClientSecret;

    @Value("${patient.client.id}")
    private String patientClientId;

    @Value("${patient.client.secret}")
    private String patientClientSecret;


    @PostMapping(value = "/doctor-authenticate")
    public ResponseEntity<?> doctorAuthenticate(@RequestBody AuthRequest authRequest){
        System.out.println("called api");
        if(hospitalClientId.equals(authRequest.getUsername()) && hospitalClientSecret.equals(authRequest.getPassword())){
            String token=jwtService.createToken(hospitalClientId);
            System.out.print(token);
            return ResponseEntity.ok(token);
        }
        ResponseEntity<String> resp=new ResponseEntity<>("Unauthorized",HttpStatus.UNAUTHORIZED);
        return resp;
    }

    @PostMapping(value = "/patient-authenticate")
    public ResponseEntity<?> patientAuthenticate(@RequestBody AuthRequest authRequest){
        System.out.println("called api");
        if(patientClientId.equals(authRequest.getUsername()) && patientClientSecret.equals(authRequest.getPassword())){
            String token=jwtService.createToken(patientClientId);
            System.out.print(token);
            return ResponseEntity.ok(token);
        }
        ResponseEntity<String> resp=new ResponseEntity<>("Unauthorized",HttpStatus.UNAUTHORIZED);
        return resp;
    }



    @GetMapping(value="/get-granted-consents/{doctorId}")
    public ResponseEntity<?> getGrantedConsents(@PathVariable("doctorId") String doctorId){
        List<GrantedConsentResponse> grantedConsentResponses=consentManagerService.getGrantedConsents(doctorId);
        return ResponseEntity.ok(grantedConsentResponses);
    }

    @PostMapping(value="/create-consent")
    public ResponseEntity<?> createConsent(@RequestBody CreateConsent createConsent){
        String consentId=consentManagerService.createConsent(createConsent);
        if(consentId==null){
            ResponseEntity<String> resp=new ResponseEntity<>("Error", HttpStatus.INTERNAL_SERVER_ERROR);
            return resp;
        }
        return ResponseEntity.ok(consentId);
    }

    @PostMapping(value = "/validate-consent/{consentId}")
    public ResponseEntity<?> validateConsent(@PathVariable("consentId") String consentId){
        ValidateConsentResponse validateConsentResponse=consentManagerService.validateConsent(consentId);
        return ResponseEntity.ok(validateConsentResponse);
    }

    @GetMapping(value = "/retrieve-consents/{patientId}")
    public ResponseEntity<?> retrieveConsents(@PathVariable("patientId") String patientId){
        List<ConsentDto> consentDtoList= consentManagerService.retrieveConsents(patientId);
        return ResponseEntity.ok(consentDtoList);
    }

    @PostMapping(value = "/delegate-consent/{doctorId}/{consentId}")
    public ResponseEntity<?> delegateConsent(@PathVariable("doctorId") String doctorId,@PathVariable("consentId") String consentId){
        String patientId=consentManagerService.delegateConsent(doctorId,consentId);
        return ResponseEntity.ok(patientId);
    }

    @PostMapping(value="/revoke-consent/{consentId}")
    public ResponseEntity<?> revokeConsent(@PathVariable("consentId") String consentId){
        String status=consentManagerService.revokeConsent(consentId);
        return ResponseEntity.ok(status);
    }
}
