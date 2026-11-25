package ma.ensam.campusconnect.repositories;

import ma.ensam.campusconnect.entities.RoleClub;
import ma.ensam.campusconnect.enums.TypeRoleClub;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleClubLectureRepository extends JpaRepository<RoleClub, Long> {
    boolean existsByUtilisateurIdAndClubId(Long utilisateurId, Long clubId);
    boolean existsByUtilisateurIdAndClubIdAndRole(Long utilisateurId, Long clubId, TypeRoleClub typeRoleClub);
    boolean existsByUtilisateurId(Long utilisateurId);
}