package ma.ensam.campusconnect.repositories;

import ma.ensam.campusconnect.entities.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UtilisateurRepository extends JpaRepository<Utilisateur,Long> {
    boolean existsByEmail(String email);
    Optional<Utilisateur> findByEmail(String email); // optional est une classe java qui permet de representer une valeur qui peut etre presente ou absente
    Optional<Utilisateur> findById(Long id);
    boolean existsById(Long id);
}
