package ma.ensam.campusconnect.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProfileView {
    private String prenom;
    private String nom;
    private String initiales;
    private String email;
    private String photoUrl;
    private long followingCount;
    private long membershipCount;
    private long interactionCount;
}