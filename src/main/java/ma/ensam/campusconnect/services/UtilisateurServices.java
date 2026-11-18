package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.RoleGlobal;
import ma.ensam.campusconnect.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service

public class UtilisateurServices {
    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public void ajouterUtilisateur(Utilisateur utilisateur){
        if(utilisateurRepository.existsByEmail(utilisateur.getEmail())){
            throw new RuntimeException("Cet email existe déjà");

            }
        utilisateur.setRolGlobal(RoleGlobal.STUDENT);
        utilisateur.setActive(false);
        utilisateur.setDateCreation(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);
    }




            }




