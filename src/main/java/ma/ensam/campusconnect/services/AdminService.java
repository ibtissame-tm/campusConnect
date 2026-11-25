package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.dto.ActiviteView;
import ma.ensam.campusconnect.dto.ClubView;
import ma.ensam.campusconnect.dto.StudentRowView;
import ma.ensam.campusconnect.dto.StudentView;
import ma.ensam.campusconnect.entities.Club;
import ma.ensam.campusconnect.entities.Publication;
import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.RoleGlobal;
import ma.ensam.campusconnect.enums.StatutAdhesion;
import ma.ensam.campusconnect.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class AdminService {

    private final UtilisateurRepository utilisateurRepository;
    private final ClubRepository clubRepository;
    private final PublicationRepository publicationRepository;
    private final AbonnementRepository abonnementRepository;
    private final AdhesionRepository adhesionRepository;

    public AdminService(UtilisateurRepository utilisateurRepository,
                        ClubRepository clubRepository,
                        PublicationRepository publicationRepository,
                        AbonnementRepository abonnementRepository,
                        AdhesionRepository adhesionRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.clubRepository = clubRepository;
        this.publicationRepository = publicationRepository;
        this.abonnementRepository = abonnementRepository;
        this.adhesionRepository = adhesionRepository;
    }

    @Transactional(readOnly = true)
    public Map<String, Long> stats() {
        long etudiants = utilisateurRepository.findAll().stream()
                .filter(u -> u.getRoleGlobal() == RoleGlobal.ETUDIANT)
                .count();
        return Map.of(
                "studentsCount", etudiants,
                "clubsCount", clubRepository.count(),
                "postsCount", publicationRepository.count(),
                "pendingReportsCount", 0L);   // branché à l'étape 3 (signalements)
    }

    /** Publications (non masquées) des 7 derniers jours, pour le graphique. */
    @Transactional(readOnly = true)
    public List<ActiviteView> activite() {
        LocalDate aujourdHui = LocalDate.now();
        List<Publication> publications = publicationRepository.findAll();

        List<LocalDate> jours = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            jours.add(aujourdHui.minusDays(i));
        }

        List<Long> comptes = jours.stream()
                .map(j -> publications.stream()
                        .filter(p -> !p.isMasquee()
                                && p.getDateCreation() != null
                                && p.getDateCreation().toLocalDate().equals(j))
                        .count())
                .toList();

        long max = comptes.stream().mapToLong(Long::longValue).max().orElse(0);

        List<ActiviteView> resultat = new ArrayList<>();
        for (int i = 0; i < jours.size(); i++) {
            long c = comptes.get(i);
            int pourcent = max == 0 ? 3 : (int) Math.max(8, c * 100 / max);
            resultat.add(new ActiviteView(libelleJour(jours.get(i)), c, pourcent));
        }
        return resultat;
    }

    @Transactional(readOnly = true)
    public List<ClubView> clubs() {
        return clubRepository.findAll().stream()
                .map(c -> {
                    ClubView v = ClubView.leger(c);
                    v.setFollowersCount(abonnementRepository.countByClubId(c.getId()));
                    v.setMembersCount(adhesionRepository
                            .countByClubIdAndStatut(c.getId(), StatutAdhesion.ACTIVE));
                    v.setPostsCount(publicationRepository.countByClubId(c.getId()));
                    return v;
                })
                .sorted(Comparator.comparing(
                        (ClubView v) -> v.getNom() == null ? "" : v.getNom(),
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<StudentRowView> etudiants(String q) {
        String recherche = q == null ? "" : q.trim().toLowerCase();
        List<StudentRowView> resultat = new ArrayList<>();

        for (Utilisateur u : utilisateurRepository.findAll()) {
            if (u.getRoleGlobal() != RoleGlobal.ETUDIANT) continue;

            StudentView s = new StudentView(u);
            String nomComplet = (s.getPrenom() + " " + s.getNom()).trim();

            boolean correspond = recherche.isEmpty()
                    || nomComplet.toLowerCase().contains(recherche)
                    || s.getEmail().toLowerCase().contains(recherche);
            if (!correspond) continue;

            long clubsCount = adhesionRepository.findByUtilisateurId(u.getId()).stream()
                    .filter(a -> a.getStatut() == StatutAdhesion.ACTIVE)
                    .count();
            resultat.add(new StudentRowView(u.getId(), nomComplet, s.getInitiales(), clubsCount));

            if (resultat.size() >= 100) break;
        }
        return resultat;
    }

    @Transactional
    public void creerClub(String nom, String description) {
        if (nom == null || nom.isBlank()) {
            throw new IllegalArgumentException("Le nom du club est obligatoire.");
        }
        String nomPropre = nom.trim();
        if (clubRepository.existsByNom(nomPropre)) {
            throw new IllegalArgumentException("Un club avec ce nom existe déjà.");
        }
        Club club = new Club();
        club.setNom(nomPropre);
        club.setDescription(description == null ? null : description.trim());
        club.setActive(true);
        clubRepository.save(club);
    }

    private String libelleJour(LocalDate d) {
        String l = d.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.FRENCH).replace(".", "");
        return l.isEmpty() ? "" : Character.toUpperCase(l.charAt(0)) + l.substring(1);
    }
}