package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.entities.Adhesion;
import ma.ensam.campusconnect.repositories.AdhesionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdhesionService {

    private final AdhesionRepository adhesionRepository;

    public AdhesionService(AdhesionRepository adhesionRepository) {
        this.adhesionRepository = adhesionRepository;
    }

    public List<Adhesion> getAllAdhesions() {
        return adhesionRepository.findAll();
    }

    public Adhesion getAdhesionById(Long id) {
        return adhesionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Adhésion introuvable"));
    }

    public Adhesion saveAdhesion(Adhesion adhesion) {
        return adhesionRepository.save(adhesion);
    }

    public void deleteAdhesion(Long id) {
        adhesionRepository.deleteById(id);
    }
}