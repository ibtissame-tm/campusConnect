package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.dto.ClubView;
import ma.ensam.campusconnect.dto.MembershipView;
import ma.ensam.campusconnect.entities.Abonnement;
import ma.ensam.campusconnect.entities.Adhesion;
import ma.ensam.campusconnect.entities.Club;
import ma.ensam.campusconnect.entities.RoleClub;
import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.StatutAdhesion;
import ma.ensam.campusconnect.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TableauDeBordService {

    private final UtilisateurRepository utilisateurRepository;
    private final ClubRepository clubRepository;
    private final AbonnementRepository abonnementRepository;
    private final AdhesionRepository adhesionRepository;
    private final RoleClubRepository roleClubRepository;

    public TableauDeBordService(UtilisateurRepository utilisateurRepository,
                                ClubRepository clubRepository,
                                AbonnementRepository abonnementRepository,
                                AdhesionRepository adhesionRepository,
                                RoleClubRepository roleClubRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.clubRepository = clubRepository;
        this.abonnementRepository = abonnementRepository;
        this.adhesionRepository = adhesionRepository;
        this.roleClubRepository = roleClubRepository;
    }

    private Utilisateur utilisateur(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }

    @Transactional(readOnly = true)
    public Map<String, Long> stats(String email) {
        Utilisateur u = utilisateur(email);
        long suivis = abonnementRepository.findByUtilisateurId(u.getId()).size();
        List<Adhesion> adhesions = adhesionRepository.findByUtilisateurId(u.getId());
        long actives = adhesions.stream().filter(a -> a.getStatut() == StatutAdhesion.ACTIVE).count();
        long enAttente = adhesions.stream().filter(a -> a.getStatut() == StatutAdhesion.EN_ATTENTE).count();
        return Map.of(
                "followingCount", suivis,
                "activeMembershipCount", actives,
                "pendingCount", enAttente);
    }

    /** Les 3 clubs les plus suivis que je ne suis pas encore. */
    @Transactional(readOnly = true)
    public List<ClubView> recommandes(String email) {
        Utilisateur u = utilisateur(email);
        Set<Long> suivis = abonnementRepository.findByUtilisateurId(u.getId()).stream()
                .map(a -> a.getClub().getId())
                .collect(Collectors.toSet());

        return clubRepository.findAll().stream()
                .filter(Club::isActive)
                .filter(c -> !suivis.contains(c.getId()))
                .map(c -> {
                    ClubView v = ClubView.leger(c);
                    v.setFollowersCount(abonnementRepository.countByClubId(c.getId()));
                    v.setFollowedByMe(false);
                    return v;
                })
                .sorted(Comparator.comparingLong(ClubView::getFollowersCount).reversed())
                .limit(3)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MembershipView> memberships(String email) {
        Utilisateur u = utilisateur(email);

        Map<Long, String> roles = new HashMap<>();
        for (RoleClub r : roleClubRepository.findByUtilisateurId(u.getId())) {
            roles.putIfAbsent(r.getClub().getId(), r.getRole().name());
        }

        List<MembershipView> resultat = new ArrayList<>();
        for (Adhesion a : adhesionRepository.findByUtilisateurId(u.getId())) {
            String statut = a.getStatut() == null ? "EN_ATTENTE" : a.getStatut().name();
            resultat.add(new MembershipView(
                    ClubView.leger(a.getClub()), statut, roles.get(a.getClub().getId())));
        }
        return resultat;
    }

    @Transactional(readOnly = true)
    public List<ClubView> clubsSuivis(String email) {
        Utilisateur u = utilisateur(email);
        return abonnementRepository.findByUtilisateurId(u.getId()).stream()
                .map(Abonnement::getClub)
                .filter(Club::isActive)
                .map(c -> {
                    ClubView v = ClubView.leger(c);
                    v.setFollowersCount(abonnementRepository.countByClubId(c.getId()));
                    return v;
                })
                .toList();
    }
}