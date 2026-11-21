package ma.ensam.campusconnect.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;



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
public class Abonnement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateAbonnement;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @PrePersist
    public void prePersist() {
        dateAbonnement = LocalDateTime.now();
    }
}
