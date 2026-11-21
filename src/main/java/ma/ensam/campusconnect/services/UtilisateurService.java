package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.RoleGlobal;
import ma.ensam.campusconnect.repositories.ClubRepository;
import ma.ensam.campusconnect.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;

@Service

public class UtilisateurService {
    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public void ajouterUtilisateur(Utilisateur utilisateur){
        if(utilisateurRepository.existsByEmail(utilisateur.getEmail())) {
            throw new RuntimeException("L'utilisateur deja existe");
        }
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        utilisateur.setRoleGlobal(RoleGlobal.ETUDIANT);
        utilisateur.setActive(true);
        utilisateur.setDateCreation(LocalDateTime.now());
        utilisateurRepository.save(utilisateur);

    }
    public List<Utilisateur> findAllUtilisateurs() {
        return utilisateurRepository.findAll();

    }
    public Utilisateur trouverParId(Long id) {
        return utilisateurRepository.findById(id).orElseThrow(()->new RuntimeException("L'utilisateur n'existe pas"));
    }
    public Utilisateur trouverParEmail(String email) {
        return utilisateurRepository.findByEmail(email).orElseThrow(()->new RuntimeException("L'utilisateur n'existe pas"));
    }
    public void modifierUtilisateur(Long id,String nom, String prenom, String photoUrl ) {
        Utilisateur utilisateur= trouverParId(id);
        utilisateur.setNom(nom);
        utilisateur.setPrenom(prenom);
        utilisateur.setPhotoUrl(photoUrl);
        utilisateurRepository.save(utilisateur);
    }
    public void activerUtilisateur(Long id) {
        Utilisateur utilisateur = trouverParId(id);
        if(utilisateur.isActive()){
            throw new RuntimeException("L'utilisateur deja active");
        }
        utilisateur.setActive(true);
        utilisateurRepository.save(utilisateur);
    }
    public void disActiver(Long id){
        Utilisateur utilisateur = trouverParId(id);
        if(!utilisateur.isActive()){
            throw new RuntimeException("Le utilisateur est deja disactive");
        }
        utilisateur.setActive(false);
        utilisateurRepository.save(utilisateur);

    }
    public void supprimerUtilisateur(Long id) {
        Utilisateur utilisateur = trouverParId(id);
        utilisateurRepository.delete(utilisateur);

    }





}
