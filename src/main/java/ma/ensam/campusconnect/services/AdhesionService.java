package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.entities.Adhesion;
import ma.ensam.campusconnect.entities.Club;
import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.RoleGlobal;
import ma.ensam.campusconnect.enums.StatutAdhesion;
import ma.ensam.campusconnect.enums.TypeRoleClub;
import ma.ensam.campusconnect.repositories.AdhesionRepository;
import ma.ensam.campusconnect.repositories.ClubRepository;
import ma.ensam.campusconnect.repositories.RoleClubLectureRepository;
import ma.ensam.campusconnect.repositories.UtilisateurRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdhesionService {

    private final AdhesionRepository adhesionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClubRepository clubRepository;
    private final RoleClubLectureRepository roleClubLectureRepository;

    public AdhesionService(AdhesionRepository adhesionRepository,
                           UtilisateurRepository utilisateurRepository,
                           ClubRepository clubRepository,
                           RoleClubLectureRepository roleClubLectureRepository) {
        this.adhesionRepository = adhesionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.clubRepository = clubRepository;
        this.roleClubLectureRepository = roleClubLectureRepository;
    }

    public List<Adhesion> getAllAdhesions() {
        return adhesionRepository.findAll();
    }

    public Adhesion getAdhesionById(Long id) {
        return adhesionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adhésion introuvable"));
    }

    private Utilisateur utilisateurConnecte(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    private boolean peutGerer(Utilisateur u, Club club) {
        return roleClubLectureRepository.existsByUtilisateurIdAndClubIdAndRole(
                u.getId(),
                club.getId(),
                TypeRoleClub.RESPONSABLE_ADHESION
        );
    }

    /** L'étudiant demande à rejoindre un club : le statut est TOUJOURS EN_ATTENTE. */
    @Transactional
    public Adhesion demanderAdhesion(String email, Long clubId) {
        Utilisateur utilisateur = utilisateurConnecte(email);
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club introuvable"));

        Optional<Adhesion> existante =
                adhesionRepository.findByUtilisateurIdAndClubId(utilisateur.getId(), clubId);

        if (existante.isPresent()) {
            Adhesion a = existante.get();
            if (a.getStatut() != StatutAdhesion.INACTIVE) {
                throw new RuntimeException("Une demande existe déjà pour ce club");
            }
            a.setStatut(StatutAdhesion.EN_ATTENTE);   // nouvelle demande après refus ou départ
            return adhesionRepository.save(a);
        }

        Adhesion adhesion = new Adhesion();
        adhesion.setUtilisateur(utilisateur);
        adhesion.setClub(club);
        adhesion.setStatut(StatutAdhesion.EN_ATTENTE);
        return adhesionRepository.save(adhesion);
    }

    /** Seul un responsable adhésion du club (ou l'admin) peut valider. */
    @Transactional
    public void valider(Long adhesionId, String email) {
        changerStatut(adhesionId, email, StatutAdhesion.ACTIVE);
    }

    /** Même règle pour refuser. */
    @Transactional
    public void refuser(Long adhesionId, String email) {
        changerStatut(adhesionId, email, StatutAdhesion.INACTIVE);
    }

    private void changerStatut(Long adhesionId, String email, StatutAdhesion nouveauStatut) {
        Utilisateur acteur = utilisateurConnecte(email);
        Adhesion adhesion = getAdhesionById(adhesionId);

        if (!peutGerer(acteur, adhesion.getClub())) {
            throw new AccessDeniedException("Action non autorisée");
        }
        adhesion.setStatut(nouveauStatut);
        adhesionRepository.save(adhesion);
    }

    /** Supprimer : seulement sa propre adhésion, ou l'admin. */
    @Transactional
    public void supprimer(Long adhesionId, String email) {
        Utilisateur acteur = utilisateurConnecte(email);
        Adhesion adhesion = getAdhesionById(adhesionId);

        boolean proprietaire = adhesion.getUtilisateur().getId().equals(acteur.getId());
        if (!proprietaire && acteur.getRoleGlobal() != RoleGlobal.ADMIN) {
            throw new AccessDeniedException("Action non autorisée");
        }
        adhesionRepository.delete(adhesion);
    }
}