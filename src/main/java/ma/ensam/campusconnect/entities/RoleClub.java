package ma.ensam.campusconnect.entities;

import jakarta.persistence.*;
import lombok.*;
import ma.ensam.campusconnect.enums.TypeRoleClub;


import java.time.LocalDateTime;

@Entity
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"utilisateur_id", "club_id", "role"}
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleClub {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TypeRoleClub role;

    private LocalDateTime dateAttribution;

    @ManyToOne
    @JoinColumn(name = "utilisateur_id", nullable = false)
    private Utilisateur utilisateur;

    @ManyToOne
    @JoinColumn(name = "club_id", nullable = false)
    private Club club;

    @PrePersist
    public void prePersist() {
        dateAttribution = LocalDateTime.now();
    }
}
