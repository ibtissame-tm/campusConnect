package ma.ensam.campusconnect.repositories;

import ma.ensam.campusconnect.entities.RoleClub;
import ma.ensam.campusconnect.enums.TypeRoleClub;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleClubRepository extends JpaRepository<RoleClub, Long> {

    List<RoleClub> findByClubId(Long clubId);

    List<RoleClub> findByUtilisateurId(Long utilisateurId);

    boolean existsByUtilisateurIdAndClubIdAndRole(
            Long utilisateurId,
            Long clubId,
            TypeRoleClub role
    );
}