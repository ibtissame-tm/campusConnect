package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.dto.ClubView;
import ma.ensam.campusconnect.dto.PostView;
import ma.ensam.campusconnect.entities.Adhesion;
import ma.ensam.campusconnect.entities.Publication;
import ma.ensam.campusconnect.entities.RoleClub;
import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.RoleGlobal;
import ma.ensam.campusconnect.enums.StatutAdhesion;
import ma.ensam.campusconnect.enums.TypePublication;
import ma.ensam.campusconnect.enums.Visibilite;
import ma.ensam.campusconnect.repositories.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class PublicationAffichageService {

    private static final int LIMITE = 50;

    private final PublicationRepository publicationRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final AbonnementRepository abonnementRepository;
    private final AdhesionRepository adhesionRepository;
    private final RoleClubRepository roleClubRepository;

    public PublicationAffichageService(PublicationRepository publicationRepository,
                                       UtilisateurRepository utilisateurRepository,
                                       AbonnementRepository abonnementRepository,
                                       AdhesionRepository adhesionRepository,
                                       RoleClubRepository roleClubRepository) {
        this.publicationRepository = publicationRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.abonnementRepository = abonnementRepository;
        this.adhesionRepository = adhesionRepository;
        this.roleClubRepository = roleClubRepository;
    }

    // ===================== Fils de publications =====================

    /** Home : clubs suivis + clubs dont je suis membre + annonces de l'école. */
    @Transactional(readOnly = true)
    public List<PostView> accueil(String email, String type) {
        Utilisateur u = utilisateur(email);
        Set<Long> suivis = clubsSuivis(u);
        Set<Long> membre = clubsMembre(u);
        return versVues(visibles(u, membre)
                .filter(p -> p.getClub() == null
                        || suivis.contains(p.getClub().getId())
                        || membre.contains(p.getClub().getId()))
                .filter(p -> correspondType(p, type)));
    }

    /** Community : tout ce que je suis autorisé à voir. */
    @Transactional(readOnly = true)
    public List<PostView> communaute(String email, String type, String visibilite) {
        Utilisateur u = utilisateur(email);
        return versVues(visibles(u, clubsMembre(u))
                .filter(p -> correspondType(p, type))
                .filter(p -> visibilite == null || visibilite.isBlank()
                        || visibiliteDe(p).name().equals(visibilite)));
    }

    /** Competitions : scope = all | following. */
    @Transactional(readOnly = true)
    public List<PostView> competitions(String email, String scope) {
        Utilisateur u = utilisateur(email);
        Set<Long> suivis = clubsSuivis(u);
        Set<Long> membre = clubsMembre(u);
        boolean seulementMesClubs = "following".equals(scope);
        return versVues(visibles(u, membre)
                .filter(p -> typeDe(p) == TypePublication.COMPETITION)
                .filter(p -> !seulementMesClubs
                        || (p.getClub() != null
                        && (suivis.contains(p.getClub().getId())
                        || membre.contains(p.getClub().getId())))));
    }

    /** Page d'un club. */
    @Transactional(readOnly = true)
    public List<PostView> duClub(String email, Long clubId) {
        Utilisateur u = utilisateur(email);
        return versVues(visibles(u, clubsMembre(u))
                .filter(p -> p.getClub() != null && p.getClub().getId().equals(clubId)));
    }

    // ===================== Règles métier =====================

    /**
     * Cœur des règles :
     *  - une publication masquée n'est jamais renvoyée ;
     *  - une publication d'un club désactivé n'est pas renvoyée ;
     *  - une publication PRIVEE n'est renvoyée qu'aux membres actifs du club (ou responsables) ;
     *  - l'admin voit tout, comme dans ton PublicationService.
     */
    private Stream<Publication> visibles(Utilisateur u, Set<Long> clubsMembre) {
        boolean admin = u.getRoleGlobal() == RoleGlobal.ADMIN;
        return publicationRepository.findByMasqueeFalseOrderByDateCreationDesc().stream()
                .filter(p -> p.getClub() == null || p.getClub().isActive())
                .filter(p -> admin
                        || p.getVisibilite() != Visibilite.PRIVEE
                        || (p.getClub() != null && clubsMembre.contains(p.getClub().getId())));
    }

    private Set<Long> clubsSuivis(Utilisateur u) {
        return abonnementRepository.findByUtilisateurId(u.getId()).stream()
                .map(a -> a.getClub().getId())
                .collect(Collectors.toSet());
    }

    /** Clubs où je suis membre ACTIF, ou responsable. */
    private Set<Long> clubsMembre(Utilisateur u) {
        Set<Long> ids = new HashSet<>();
        for (Adhesion a : adhesionRepository.findByUtilisateurId(u.getId())) {
            if (a.getStatut() == StatutAdhesion.ACTIVE) ids.add(a.getClub().getId());
        }
        for (RoleClub r : roleClubRepository.findByUtilisateurId(u.getId())) {
            ids.add(r.getClub().getId());
        }
        return ids;
    }

    // ===================== Conversion en vues =====================

    private List<PostView> versVues(Stream<Publication> flux) {
        return flux.limit(LIMITE).map(this::versVue).toList();
    }

    private PostView versVue(Publication p) {
        PostView v = new PostView();
        v.setId(p.getId());
        v.setTitre(titreOuDefaut(p));
        v.setContenu(p.getContenu());
        v.setType(typeDe(p).name());
        v.setVisibilite(visibiliteDe(p).name());
        v.setDatePublication(dateRelative(p.getDateCreation()));
        v.setImageUrl(p.getImageUrl() == null || p.getImageUrl().isBlank() ? null : p.getImageUrl());
        v.setClub(p.getClub() == null ? ClubView.ecole() : ClubView.leger(p.getClub()));
        return v;
    }

    private TypePublication typeDe(Publication p) {
        return p.getTypePublication() == null ? TypePublication.ACTUALITE : p.getTypePublication();
    }

    private Visibilite visibiliteDe(Publication p) {
        return p.getVisibilite() == null ? Visibilite.PUBLIQUE : p.getVisibilite();
    }

    private boolean correspondType(Publication p, String type) {
        return type == null || type.isBlank() || typeDe(p).name().equals(type);
    }

    private String titreOuDefaut(Publication p) {
        if (p.getTitre() != null && !p.getTitre().isBlank()) return p.getTitre();
        String c = p.getContenu() == null ? "" : p.getContenu().trim();
        if (c.isEmpty()) return "Publication";
        return c.length() <= 60 ? c : c.substring(0, 60).trim() + "…";
    }

    private String dateRelative(LocalDateTime d) {
        if (d == null) return "";
        Duration diff = Duration.between(d, LocalDateTime.now());
        long min = diff.toMinutes();
        if (min < 1) return "À l'instant";
        if (min < 60) return "Il y a " + min + " min";
        long h = diff.toHours();
        if (h < 24) return "Il y a " + h + " h";
        long j = diff.toDays();
        if (j < 7) return "Il y a " + j + (j == 1 ? " jour" : " jours");
        return d.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
    }

    private Utilisateur utilisateur(String email) {
        return utilisateurRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));
    }
}