package ma.ensam.campusconnect.services;
import ma.ensam.campusconnect.entities.Club;
import ma.ensam.campusconnect.entities.Publication;
import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.*;
import ma.ensam.campusconnect.repositories.AdhesionLectureRepository;
import ma.ensam.campusconnect.repositories.PublicationRepository;
import ma.ensam.campusconnect.repositories.RoleClubLectureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
@Service
public class PublicationService {
    @Autowired
    private PublicationRepository publicationRepository;
    @Autowired
    private ClubService clubService;
    @Autowired
    private RoleClubLectureRepository roleClubLectureRepository;
    @Autowired
    private AdhesionLectureRepository adhesionLectureRepository;

    @Transactional
    public Publication ajouterPublication(Long clubId, String contenu, String imageUrl,
                                          Visibilite visibilite,
                                          TypePublication typePublication,
                                          Utilisateur auteur) {
        Publication publication = new Publication();
        publication.setContenu(contenu);
        publication.setImageUrl(imageUrl);
        publication.setTypePublication(
                typePublication != null ? typePublication : TypePublication.ACTUALITE);
        publication.setAuteur(auteur);
        publication.setDateCreation(LocalDateTime.now());

        if (clubId == null) {
            if (auteur.getRoleGlobal() != RoleGlobal.ADMIN) {
                throw new AccessDeniedException("Réservé à l'administrateur");
            }
            publication.setVisibilite(Visibilite.PUBLIQUE);
        } else {
            Club club = clubService.trouverParId(clubId);
            verifierResponsableMedia(auteur, clubId);
            publication.setClub(club);
            publication.setVisibilite(
                    visibilite != null ? visibilite : Visibilite.PUBLIQUE);
        }
        return publicationRepository.save(publication);
    }
    public List<Publication> listerPublicationsClub(Long clubId, Utilisateur utilisateur) {
        clubService.trouverParId(clubId);
        if (peutVoirPublicationsPrivees(utilisateur, clubId)) {
            return publicationRepository.findByClubIdOrderByDateCreationDesc(clubId);
        }
        return publicationRepository
                .findByClubIdAndVisibiliteOrderByDateCreationDesc(clubId, Visibilite.PUBLIQUE);
    }

    public List<Publication> listerCompetitions() {
        return publicationRepository
                .findByTypePublicationAndVisibiliteOrderByDateCreationDesc(
                        TypePublication.COMPETITION, Visibilite.PUBLIQUE);
    }

    public Publication trouverParId(Long id) {
        return publicationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("La publication n'existe pas"));
    }

    @Transactional
    public Publication modifierPublication(Long id, Utilisateur demandeur,
                                           String contenu, Visibilite visibilite) {
        Publication publication = trouverParId(id);
        verifierDroitDeGestion(demandeur, publication);

        if (contenu != null) {
            publication.setContenu(contenu);
        }
        if (visibilite != null) {
            if (publication.getClub() == null && visibilite == Visibilite.PRIVEE) {
                throw new RuntimeException("Une publication de l'école est toujours publique");
            }
            publication.setVisibilite(visibilite);
        }
        publication.setDateModification(LocalDateTime.now());
        return publicationRepository.save(publication);
    }

    @Transactional
    public void supprimerPublication(Long id, Utilisateur demandeur) {
        Publication publication = trouverParId(id);
        verifierDroitDeGestion(demandeur, publication);
        publicationRepository.delete(publication);
    }



    private boolean peutVoirPublicationsPrivees(Utilisateur u, Long clubId) {
        if (u.getRoleGlobal() == RoleGlobal.ADMIN) {
            return true;
        }
        return roleClubLectureRepository.existsByUtilisateurIdAndClubId(u.getId(), clubId)
                || adhesionLectureRepository.existsByUtilisateurIdAndClubIdAndStatut(
                u.getId(), clubId, StatutAdhesion.ACTIVE);
    }

    private void verifierResponsableMedia(Utilisateur u, Long clubId) {
        boolean ok = roleClubLectureRepository.existsByUtilisateurIdAndClubIdAndRole(
                u.getId(), clubId, TypeRoleClub.RESPONSABLE_MEDIA);
        if (!ok) {
            throw new AccessDeniedException("Vous n'êtes pas responsable média de ce club");
        }
    }

    private void verifierDroitDeGestion(Utilisateur u, Publication p) {
        if (p.getClub() == null) {
            if (u.getRoleGlobal() != RoleGlobal.ADMIN) {
                throw new AccessDeniedException("Réservé à l'administrateur");
            }
        } else {
            verifierResponsableMedia(u, p.getClub().getId());
        }
    }
}