package ma.ensam.campusconnect.repositories;

import ma.ensam.campusconnect.entities.Adhesion;
import ma.ensam.campusconnect.enums.StatutAdhesion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AdhesionRepository extends JpaRepository<Adhesion, Long> {

    Optional<Adhesion> findByUtilisateurIdAndClubId(Long utilisateurId, Long clubId);

    long countByClubIdAndStatut(Long clubId, StatutAdhesion statut);

    List<Adhesion> findByUtilisateurId(Long utilisateurId);

    List<Adhesion> findByClubIdAndStatut(
            Long clubId,
            StatutAdhesion statut
    );
}