package ma.ensam.campusconnect.entities;
import lombok.*;
import ma.ensam.campusconnect.enums.TypePublication;
import ma.ensam.campusconnect.enums.Visibilite;
import jakarta.persistence.*;
import java.time.LocalDateTime;
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Publication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String titre;
    private String contenu;
    private String imageUrl;
    private boolean masquee;
    @Enumerated(EnumType.STRING)
    private Visibilite  visibilite;
    private LocalDateTime dateCreation;
    private LocalDateTime dateModification;
    @Enumerated(EnumType.STRING)
    private TypePublication typePublication;

    @PrePersist
    public void prePersist() {
        dateCreation = LocalDateTime.now();
        dateModification = dateCreation;
    }
    @PreUpdate
    public void preUpdate() {
        dateModification = LocalDateTime.now();
    }
    @ManyToOne
    private Club club;
    @ManyToOne
    private Utilisateur auteur;


}