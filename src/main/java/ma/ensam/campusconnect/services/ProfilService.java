package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.dto.ProfileView;
import ma.ensam.campusconnect.dto.StudentView;
import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.StatutAdhesion;
import ma.ensam.campusconnect.enums.TypeRoleClub;
import ma.ensam.campusconnect.repositories.AbonnementRepository;
import ma.ensam.campusconnect.repositories.AdhesionRepository;
import ma.ensam.campusconnect.repositories.RoleClubRepository;
import ma.ensam.campusconnect.repositories.UtilisateurRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfilService {

    private final UtilisateurRepository utilisateurRepository;
    private final AbonnementRepository abonnementRepository;
    private final AdhesionRepository adhesionRepository;
    private final RoleClubRepository roleClubRepository;
    private final PasswordEncoder passwordEncoder;

    public ProfilService(UtilisateurRepository utilisateurRepository,
                         AbonnementRepository abonnementRepository,
                         AdhesionRepository adhesionRepository,
                         RoleClubRepository roleClubRepository,
                         PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.abonnementRepository = abonnementRepository;
        this.adhesionRepository = adhesionRepository;
        this.roleClubRepository = roleClubRepository;
        this.passwordEncoder = passwordEncoder;
    }

    private Utilisateur utilisateur(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    @Transactional(readOnly = true)
    public ProfileView profil(String email) {
        Utilisateur u = utilisateur(email);
        StudentView s = new StudentView(u);

        ProfileView p = new ProfileView();
        p.setPrenom(s.getPrenom());
        p.setNom(s.getNom());
        p.setInitiales(s.getInitiales());
        p.setEmail(u.getEmail());
        p.setPhotoUrl(u.getPhotoUrl() == null || u.getPhotoUrl().isBlank() ? null : u.getPhotoUrl());
        p.setFollowingCount(abonnementRepository.findByUtilisateurId(u.getId()).size());
        p.setMembershipCount(adhesionRepository.findByUtilisateurId(u.getId()).stream()
                .filter(a -> a.getStatut() == StatutAdhesion.ACTIVE)
                .count());
        p.setInteractionCount(0);   // branché à l'étape 3 (likes et commentaires)
        return p;
    }

    /** Ex. : "Responsable média · AI & Data Club". Liste vide si aucun rôle. */
    @Transactional(readOnly = true)
    public List<String> rolesLabels(String email) {
        Utilisateur u = utilisateur(email);
        return roleClubRepository.findByUtilisateurId(u.getId()).stream()
                .map(r -> (r.getRole() == TypeRoleClub.RESPONSABLE_MEDIA
                        ? "Responsable média" : "Responsable adhésions")
                        + " · " + r.getClub().getNom())
                .toList();
    }

    @Transactional
    public void changerMotDePasse(String email, String actuel, String nouveau, String confirmation) {
        if (nouveau == null || nouveau.length() < 8) {
            throw new IllegalArgumentException("Le nouveau mot de passe doit contenir au moins 8 caractères.");
        }
        if (!nouveau.equals(confirmation)) {
            throw new IllegalArgumentException("La confirmation ne correspond pas au nouveau mot de passe.");
        }
        Utilisateur u = utilisateur(email);
        if (actuel == null || !passwordEncoder.matches(actuel, u.getMotDePasse())) {
            throw new IllegalArgumentException("Le mot de passe actuel est incorrect.");
        }
        u.setMotDePasse(passwordEncoder.encode(nouveau));
        utilisateurRepository.save(u);
    }
}