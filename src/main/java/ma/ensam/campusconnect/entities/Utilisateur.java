package ma.ensam.campusconnect.entities;
import jakarta.persistence.*;
import lombok.*;
import ma.ensam.campusconnect.enums.RoleGlobal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter


public class Utilisateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String prenom;
    @Column(unique = true, nullable = false)
    private String email;
    private String motDePasse;
    private String photoUrl;
    @Enumerated(EnumType.STRING)
    private RoleGlobal roleGlobal;
    private boolean active;
    private LocalDateTime dateCreation;
    @PrePersist
    public void prePersist() {

        dateCreation = LocalDateTime.now();
    }
    @OneToMany (mappedBy = "auteur")
    private List<Publication> publicationsAuteur=new ArrayList<>();
    @OneToMany(mappedBy = "utilisateur")
    private List<Adhesion> Adhesions=new ArrayList<>();
    @OneToMany(mappedBy="utilisateur")
    private List<Abonnement>abonnements=new ArrayList<>();
    @OneToMany(mappedBy = "utilisateur")
    private List<RoleClub> roleClubs=new ArrayList<>();

}
