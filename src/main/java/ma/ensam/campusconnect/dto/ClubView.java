package ma.ensam.campusconnect.dto;

import lombok.Getter;
import lombok.Setter;
import ma.ensam.campusconnect.entities.Club;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import ma.ensam.campusconnect.dto.StudentView;

@Getter
@Setter
public class ClubView {
    private Long id;
    private String nom;
    private String description;
    private String initiales;
    private String logoUrl;
    private String coverUrl;
    private int colorIndex;              // 0 à 4 : classes CSS c0..c4
    private long followersCount;
    private long membersCount;
    private long postsCount;
    private boolean followedByMe;
    private String membershipStatus;     // null | EN_ATTENTE | ACTIVE | INACTIVE
    private List<ResponsableView> responsables = new ArrayList<>();
    private List<StudentView> membres = new ArrayList<>();

    public static String calculerInitiales(String nom) {
        if (nom == null || nom.isBlank()) return "?";
        List<String> mots = Arrays.stream(nom.trim().split("[^\\p{L}\\p{N}]+"))
                .filter(m -> !m.isEmpty())
                .toList();
        if (mots.isEmpty()) return "?";
        if (mots.size() == 1) {
            String m = mots.get(0);
            return m.substring(0, Math.min(2, m.length())).toUpperCase();
        }
        return (mots.get(0).substring(0, 1) + mots.get(1).substring(0, 1)).toUpperCase();
    }

    private static String videSiBlanc(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    /** Version légère (sans compteurs) pour les cartes de publication, la sidebar, etc. */
    public static ClubView leger(Club c) {
        ClubView v = new ClubView();
        v.setId(c.getId());
        v.setNom(c.getNom());
        v.setInitiales(calculerInitiales(c.getNom()));
        v.setLogoUrl(videSiBlanc(c.getLogoUrl()));
        v.setCoverUrl(videSiBlanc(c.getCouvertureUrl()));
        v.setColorIndex((int) (c.getId() % 5));
        return v;
    }

    /** Pseudo-club pour les annonces de l'école (publication sans club). id = null : pas de page club. */
    public static ClubView ecole() {
        ClubView v = new ClubView();
        v.setNom("ENSAM Casablanca");
        v.setInitiales("EN");
        v.setColorIndex(2);
        return v;
    }
}