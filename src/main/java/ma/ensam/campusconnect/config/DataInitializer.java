package ma.ensam.campusconnect.config;

import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.RoleGlobal;
import ma.ensam.campusconnect.repositories.UtilisateurRepository;
import ma.ensam.campusconnect.services.UtilisateurService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UtilisateurService utilisateurService;
    private final UtilisateurRepository utilisateurRepository;

    public DataInitializer(UtilisateurService utilisateurService,
                           UtilisateurRepository utilisateurRepository) {
        this.utilisateurService = utilisateurService;
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public void run(String... args) {
        creerSiAbsent("Admin", "ENSAM", "admin@ensam.ma", "Admin123!", RoleGlobal.ADMIN);
        creerSiAbsent("Aya", "Test", "etudiant@ensam.ma", "Etudiant123!", RoleGlobal.ETUDIANT);
    }

    private void creerSiAbsent(String prenom, String nom, String email,
                               String motDePasse, RoleGlobal role) {
        if (utilisateurRepository.existsByEmail(email)) {
            return;   // déjà créé : on ne fait rien
        }
        Utilisateur u = new Utilisateur();
        u.setPrenom(prenom);
        u.setNom(nom);
        u.setEmail(email);
        u.setMotDePasse(motDePasse);   // haché par creerUtilisateur
        utilisateurService.creerUtilisateur(u, role);
    }
}