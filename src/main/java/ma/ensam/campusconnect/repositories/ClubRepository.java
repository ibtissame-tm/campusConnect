package ma.ensam.campusconnect.repositories;

import ma.ensam.campusconnect.entities.Club;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ClubRepository extends JpaRepository<Club,Long> {
    Optional<Club> findById(long id);
    Optional<Club> findByName(String name);
    Optional<Club> findByNameAndId(String name, String id);
    boolean existsByName(String name);


}
