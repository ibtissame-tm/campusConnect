package ma.ensam.campusconnect.repositories;

import ma.ensam.campusconnect.entities.Publication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PublicationRepository extends JpaRepository<Publication,Long> {
}
