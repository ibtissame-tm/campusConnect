package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.dto.ClubView;
import ma.ensam.campusconnect.dto.ResponsableView;
import ma.ensam.campusconnect.dto.StudentView;
import ma.ensam.campusconnect.entities.Club;
import ma.ensam.campusconnect.entities.RoleClub;
import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.StatutAdhesion;
import ma.ensam.campusconnect.enums.TypeRoleClub;
import ma.ensam.campusconnect.repositories.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import ma.ensam.campusconnect.entities.Adhesion;

@Service
public class ClubAffichageService {

    private final ClubRepository clubRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AbonnementRepository abonnementRepository;
    private final AdhesionRepository adhesionRepository;
    private final RoleClubRepository roleClubRepository;
    private final PublicationRepository publicationRepository;

    public ClubAffichageService(ClubRepository clubRepository,
                                UtilisateurRepository utilisateurRepository,
                                AbonnementRepository abonnementRepository,
                                AdhesionRepository adhesionRepository,
                                RoleClubRepository roleClubRepository,
                                PublicationRepository publicationRepository) {
        this.clubRepository = clubRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.abonnementRepository = abonnementRepository;
        this.adhesionRepository = adhesionRepository;
        this.roleClubRepository = roleClubRepository;
        this.publicationRepository = publicationRepository;
    }

    private Utilisateur utilisateurConnecte(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    @Transactional(readOnly = true)
    public List<ClubView> lister(String email, String q, String filter, String sort) {
        Utilisateur utilisateur = utilisateurConnecte(email);
        String recherche = q == null ? "" : q.trim().toLowerCase();

        List<ClubView> resultat = clubRepository.findAll().stream()
                .filter(Club::isActive)
                .filter(c -> recherche.isEmpty()
                        || (c.getNom() != null && c.getNom().toLowerCase().contains(recherche)))
                .map(c -> construire(c, utilisateur))
                .filter(v -> switch (filter) {
                    case "following" -> v.isFollowedByMe();
                    case "member" -> "ACTIVE".equals(v.getMembershipStatus());
                    default -> true;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        if ("name".equals(sort)) {
            resultat.sort(Comparator.comparing(
                    (ClubView v) -> v.getNom() == null ? "" : v.getNom(),
                    String.CASE_INSENSITIVE_ORDER));
        } else {
            resultat.sort(Comparator.comparingLong(ClubView::getFollowersCount).reversed());
        }
        return resultat;
    }


    @Transactional(readOnly = true)
    public ClubView detail(String email, Long clubId) {
        Utilisateur utilisateur = utilisateurConnecte(email);

        Club club = clubRepository.findById(clubId)
                .filter(Club::isActive)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Club introuvable"));

        ClubView vue = construire(club, utilisateur);

        // 1. Récupérer les rôles attribués dans ce club
        List<RoleClub> rolesClub = roleClubRepository.findByClubId(club.getId());

        // 2. Construire la liste des responsables
        for (RoleClub r : rolesClub) {
            Utilisateur ru = r.getUtilisateur();

            String nom = ((ru.getPrenom() == null ? "" : ru.getPrenom()) + " "
                    + (ru.getNom() == null ? "" : ru.getNom())).trim();

            String label = r.getRole() == TypeRoleClub.RESPONSABLE_MEDIA
                    ? "Responsable média"
                    : "Responsable adhésions";

            // Éviter les doublons dans l'équipe
            boolean dejaPresent = vue.getResponsables().stream()
                    .anyMatch(resp -> resp.getNom().equals(nom)
                            && resp.getInitiales().equals(new StudentView(ru).getInitiales())
                            && resp.getRoleLabel().equals(label));

            if (!dejaPresent) {
                vue.getResponsables().add(
                        new ResponsableView(nom, new StudentView(ru).getInitiales(), label)
                );
            }
        }

        // 3. Récupérer uniquement les adhésions actives
        List<Adhesion> adhesionsActives =
                adhesionRepository.findByClubIdAndStatut(
                        club.getId(), StatutAdhesion.ACTIVE);

        // 4. Construire la liste des membres sans doublons
        for (Adhesion adhesion : adhesionsActives) {
            Utilisateur membre = adhesion.getUtilisateur();

            List<String> rolesMembre = rolesClub.stream()
                    .filter(r -> r.getUtilisateur().getId().equals(membre.getId()))
                    .map(r -> r.getRole().name())
                    .distinct()
                    .toList();

            vue.getMembres().add(new StudentView(membre, rolesMembre));
        }

        return vue;
    }


    private ClubView construire(Club c, Utilisateur utilisateur) {
        ClubView v = new ClubView();
        v.setId(c.getId());
        v.setNom(c.getNom());
        v.setDescription(c.getDescription());
        v.setInitiales(ClubView.calculerInitiales(c.getNom()));
        v.setLogoUrl(c.getLogoUrl());
        v.setCoverUrl(c.getCouvertureUrl());
        v.setColorIndex((int) (c.getId() % 5));
        v.setFollowersCount(abonnementRepository.countByClubId(c.getId()));
        v.setMembersCount(adhesionRepository.countByClubIdAndStatut(c.getId(), StatutAdhesion.ACTIVE));
        v.setPostsCount(publicationRepository.countByClubIdAndMasqueeFalse(c.getId()));
        v.setFollowedByMe(abonnementRepository
                .existsByUtilisateurIdAndClubId(utilisateur.getId(), c.getId()));
        v.setMembershipStatus(adhesionRepository
                .findByUtilisateurIdAndClubId(utilisateur.getId(), c.getId())
                .map(a -> a.getStatut())
                .map(Enum::name)
                .orElse(null));
        return v;
    }
}