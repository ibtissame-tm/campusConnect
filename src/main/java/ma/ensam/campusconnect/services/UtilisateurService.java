package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.RoleGlobal;
import ma.ensam.campusconnect.repositories.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public Utilisateur creerUtilisateur(Utilisateur utilisateur, RoleGlobal role) {
        String email = utilisateur.getEmail().trim().toLowerCase();
        if (utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }
        if (utilisateur.getMotDePasse() == null || utilisateur.getMotDePasse().isBlank()) {
            throw new RuntimeException("Le mot de passe est obligatoire");
        }
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(passwordEncoder.encode(utilisateur.getMotDePasse()));
        utilisateur.setRoleGlobal(role != null ? role : RoleGlobal.ETUDIANT);
        utilisateur.setActive(true);
        utilisateur.setDateCreation(LocalDateTime.now());
        return utilisateurRepository.save(utilisateur);
    }

    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    public Utilisateur trouverParId(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("L'utilisateur n'existe pas"));
    }

    public Utilisateur trouverParEmail(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("L'utilisateur n'existe pas"));
    }

    @Transactional
    public Utilisateur modifierUtilisateur(Long id, String prenom, String nom, String photoUrl) {
        Utilisateur utilisateur = trouverParId(id);
        if (prenom != null) utilisateur.setPrenom(prenom);
        if (nom != null) utilisateur.setNom(nom);
        if (photoUrl != null) utilisateur.setPhotoUrl(photoUrl);
        return utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void activerUtilisateur(Long id) {
        Utilisateur utilisateur = trouverParId(id);
        if (utilisateur.isActive()) {
            throw new RuntimeException("L'utilisateur est déjà actif");
        }
        utilisateur.setActive(true);
        utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void desactiverUtilisateur(Long id) {
        Utilisateur utilisateur = trouverParId(id);
        if (!utilisateur.isActive()) {
            throw new RuntimeException("L'utilisateur est déjà désactivé");
        }
        utilisateur.setActive(false);
        utilisateurRepository.save(utilisateur);
    }

    @Transactional
    public void supprimerUtilisateur(Long id) {
        utilisateurRepository.delete(trouverParId(id));
    }
}