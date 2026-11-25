
package ma.ensam.campusconnect.dto;

import lombok.Getter;
import ma.ensam.campusconnect.entities.Utilisateur;

import java.util.ArrayList;
import java.util.List;

@Getter
public class StudentView {

    private final String prenom;
    private final String nom;
    private final String email;
    private final String initiales;

    // Rôles fonctionnels dans le club
    private final List<String> roles;

    public StudentView(Utilisateur u) {
        this(u, new ArrayList<>());
    }

    public StudentView(Utilisateur u, List<String> roles) {
        this.prenom = u.getPrenom() == null ? "" : u.getPrenom();
        this.nom = u.getNom() == null ? "" : u.getNom();
        this.email = u.getEmail() == null ? "" : u.getEmail();

        String p = prenom.isEmpty() ? "" : prenom.substring(0, 1);
        String n = nom.isEmpty() ? "" : nom.substring(0, 1);

        this.initiales = (p + n).toUpperCase();
        this.roles = roles == null ? new ArrayList<>() : new ArrayList<>(roles);
    }
}
