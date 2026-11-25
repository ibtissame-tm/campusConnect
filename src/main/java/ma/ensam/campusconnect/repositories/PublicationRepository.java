package ma.ensam.campusconnect.repositories;

import ma.ensam.campusconnect.entities.Publication;
import ma.ensam.campusconnect.enums.TypePublication;
import ma.ensam.campusconnect.enums.Visibilite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PublicationRepository extends JpaRepository<Publication, Long> {
    Optional<Publication> findById(long id);
    List<Publication> findByClubIdOrderByDateCreationDesc(Long clubId);
    List<Publication> findByClubIdAndVisibiliteOrderByDateCreationDesc(Long clubId, Visibilite visibilite);
    List<Publication> findByTypePublicationAndVisibiliteOrderByDateCreationDesc(
            TypePublication type, Visibilite visibilite);
    long countByClubId(Long clubId);
    long countByClubIdAndMasqueeFalse(Long clubId);
    List<Publication> findByMasqueeFalseOrderByDateCreationDesc();
}