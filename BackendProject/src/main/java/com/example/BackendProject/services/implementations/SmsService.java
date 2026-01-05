package com.example.BackendProject.services.implementations;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    @Value("${twilio.account_sid:}")
    private String accountSid;

    @Value("${twilio.auth_token:}")
    private String authToken;

    @Value("${twilio.phone_number:}")
    private String fromNumber;

    // Mode développement : true = simulation, false = envoi réel
    @Value("${sms.dev-mode:true}")
    private boolean devMode;

    @PostConstruct
    public void initTwilio() {
        if (!devMode && accountSid != null && !accountSid.isEmpty()) {
            Twilio.init(accountSid, authToken);
        }
    }

    public void sendSms(String toNumber, String messageBody) {
        // Mode développement : on simule juste l'envoi
        if (devMode) {
            System.out.println("========================================");
            System.out.println(" MODE DEV - SMS SIMULÉ");
            System.out.println("Destinataire : " + toNumber);
            System.out.println("Message : " + messageBody);
            System.out.println("========================================");
            return;
        }

        // Mode production : envoi réel via Twilio
        try {
            Message.creator(
                    new PhoneNumber(toNumber),
                    new PhoneNumber(fromNumber),
                    messageBody
            ).create();
            System.out.println("SMS envoyé avec succès à " + toNumber);
        } catch (Exception e) {
            System.err.println(" Erreur Twilio : " + e.getMessage());
            throw new RuntimeException("Échec de l'envoi du SMS de sécurité");
        }
    }
}