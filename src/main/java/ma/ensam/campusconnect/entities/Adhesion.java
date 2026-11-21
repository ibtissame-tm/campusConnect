package ma.ensam.campusconnect.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import ma.ensam.campusconnect.enums.StatutAdhesion;


@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"utilisateur_id", "club_id"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Adhesion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private StatutAdhesion statut;

    private LocalDateTime dateAdhesion;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @PrePersist
    public void prePersist() {
        dateAdhesion = LocalDateTime.now();
    }
}
