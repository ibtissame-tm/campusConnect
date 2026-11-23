package ma.ensam.campusconnect.repositories;

import ma.ensam.campusconnect.entities.Adhesion;
import ma.ensam.campusconnect.enums.StatutAdhesion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdhesionLectureRepository extends JpaRepository<Adhesion,Long> {
    boolean existsByUtilisateurIdAndClubIdAndStatut(Long uId, Long clId, StatutAdhesion statut);

}
