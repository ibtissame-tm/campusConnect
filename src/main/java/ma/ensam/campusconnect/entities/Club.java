package ma.ensam.campusconnect.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

public class Club {
    @Id @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;
    private String nom;
    private String description;
    private String logoUrl;
    private String couvertureUrl;
    private boolean active;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    @PrePersist
    public void prePersist() {
        dateCreation = LocalDateTime.now();
    }
    @PreUpdate
    public void preUpdate() {
        dateModification = LocalDateTime.now();
    }
    @OneToMany(mappedBy = "club")
    private List<Publication> publications;
    @OneToMany(mappedBy = "club")
    private List<Adhesion> Adhesions;
    @OneToMany(mappedBy="club")
    private List<Abonnement>abonnements;
    @OneToMany(mappedBy = "club")
    private List<RoleClub> roleClubs;



}
