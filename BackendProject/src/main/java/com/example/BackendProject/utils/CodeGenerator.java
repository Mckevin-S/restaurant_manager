package com.example.BackendProject.utils;


import com.example.BackendProject.repository.UtilisateurRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class CodeGenerator {

    private UtilisateurRepository utilisateurRepository;

    public CodeGenerator(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public long genarate(String role){
        String prefix =switch (role){
            case "MANAGER"->"MNGR";
            case "SERVEUR"->"SERV";
            case "CAISSIER"->"CAIS";
            case "CUISINIER"->"CUIS";
            default -> null;
        };
        long randomNumber = ThreadLocalRandom.current().nextInt(1000,10000);
        int year = LocalDate.now().getYear();
        long code = Long.parseLong(prefix + year+randomNumber);
        if(utilisateurRepository.existsById(code))
            return genarate(role);
        else
            return code;
    }


}
