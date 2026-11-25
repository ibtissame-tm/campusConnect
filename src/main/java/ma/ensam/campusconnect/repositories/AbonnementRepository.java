package ma.ensam.campusconnect.repositories;

import ma.ensam.campusconnect.entities.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
    boolean existsByUtilisateurIdAndClubId(Long utilisateurId, Long clubId);
    Optional<Abonnement> findByUtilisateurIdAndClubId(Long utilisateurId, Long clubId);
    long countByClubId(Long clubId);
    List<Abonnement> findByUtilisateurId(Long utilisateurId);
}