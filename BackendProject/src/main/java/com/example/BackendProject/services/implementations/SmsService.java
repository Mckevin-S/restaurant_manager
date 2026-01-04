package com.example.BackendProject.services.implementations;

import org.springframework.stereotype.Service;

@Service
public class SmsService {
    // Remplacer par vos credentials Twilio
    public void sendSms(String to, String code) {
        System.out.println("Envoi du code " + code + " au numéro " + to);
        // Logique Twilio : Message.creator(new PhoneNumber(to), ...).create();
    }
}
