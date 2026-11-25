package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.entities.Abonnement;
import ma.ensam.campusconnect.entities.Club;
import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.repositories.AbonnementRepository;
import ma.ensam.campusconnect.repositories.ClubRepository;
import ma.ensam.campusconnect.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AbonnementService {

    private final AbonnementRepository abonnementRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClubRepository clubRepository;

    public AbonnementService(AbonnementRepository abonnementRepository,
                             UtilisateurRepository utilisateurRepository,
                             ClubRepository clubRepository) {
        this.abonnementRepository = abonnementRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.clubRepository = clubRepository;
    }

    @Transactional
    public void suivre(String email, Long clubId) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club introuvable"));

        if (abonnementRepository.existsByUtilisateurIdAndClubId(utilisateur.getId(), clubId)) {
            return;   // déjà abonné : rien à faire
        }
        Abonnement abonnement = new Abonnement();
        abonnement.setUtilisateur(utilisateur);
        abonnement.setClub(club);
        abonnementRepository.save(abonnement);
    }

    @Transactional
    public void nePlusSuivre(String email, Long clubId) {
        Utilisateur utilisateur = utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
        abonnementRepository.findByUtilisateurIdAndClubId(utilisateur.getId(), clubId)
                .ifPresent(abonnementRepository::delete);
    }
}