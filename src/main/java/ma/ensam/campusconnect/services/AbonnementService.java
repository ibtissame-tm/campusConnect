package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.entities.Abonnement;
import ma.ensam.campusconnect.repositories.AbonnementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AbonnementService {

    private final AbonnementRepository abonnementRepository;

    public AbonnementService(AbonnementRepository abonnementRepository) {
        this.abonnementRepository = abonnementRepository;
    }

    public List<Abonnement> getAllAbonnements() {
        return abonnementRepository.findAll();
    }

    public Abonnement getAbonnementById(Long id) {
        return abonnementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Abonnement introuvable"));
    }

    public Abonnement saveAbonnement(Abonnement abonnement) {
        return abonnementRepository.save(abonnement);
    }

    public void deleteAbonnement(Long id) {
        abonnementRepository.deleteById(id);
    }
}