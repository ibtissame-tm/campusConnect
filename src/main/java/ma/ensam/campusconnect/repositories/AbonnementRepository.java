package ma.ensam.campusconnect.repositories;

import ma.ensam.campusconnect.entities.Abonnement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbonnementRepository extends JpaRepository<Abonnement, Long> {
}