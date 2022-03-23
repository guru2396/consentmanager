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

    @Value("${client.id}")
    private String clientId;

    @Value("${client.secret}")
    private String clientSecret;

    @PostMapping(value = "/authenticate")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest authRequest){
        System.out.println("called api");
        if(clientId.equals(authRequest.getUsername()) && clientSecret.equals(authRequest.getPassword())){
            String token=jwtService.createToken(clientId);
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
}
