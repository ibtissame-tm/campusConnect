package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.entities.Club;
import ma.ensam.campusconnect.repositories.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ClubService {

    @Autowired
    private ClubRepository clubRepository;

    @Transactional
    public Club creerClub(Club club) {
        if (clubRepository.existsByNom(club.getNom())) {
            throw new RuntimeException("Un club avec ce nom existe déjà");
        }
        club.setActive(true);
        club.setDateCreation(LocalDateTime.now());
        return clubRepository.save(club);
    }

    public Club trouverParId(Long id) {
        return clubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Le club n'existe pas"));
    }

    @Transactional
    public Club modifierClub(Long id, String nom, String description,
                             String logoUrl, String couvertureUrl) {
        Club club = trouverParId(id);
        if (nom != null && !nom.equals(club.getNom())) {
            if (clubRepository.existsByNom(nom)) {
                throw new RuntimeException("Un club avec ce nom existe déjà");
            }
            club.setNom(nom);
        }
        if (description != null) club.setDescription(description);
        if (logoUrl != null) club.setLogoUrl(logoUrl);
        if (couvertureUrl != null) club.setCouvertureUrl(couvertureUrl);
        club.setDateModification(LocalDateTime.now());
        return clubRepository.save(club);
    }

    public List<Club> obtenirTousLesClubs() {
        return clubRepository.findAll();
    }

    @Transactional
    public void supprimerClub(Long id) {
        clubRepository.delete(trouverParId(id));
    }

    @Transactional
    public void activerClub(Long id) {
        Club club = trouverParId(id);
        if (club.isActive()) {
            throw new RuntimeException("Le club est déjà actif");
        }
        club.setActive(true);
        clubRepository.save(club);
    }

    @Transactional
    public void desactiverClub(Long id) {
        Club club = trouverParId(id);
        if (!club.isActive()) {
            throw new RuntimeException("Le club est déjà désactivé");
        }
        club.setActive(false);
        clubRepository.save(club);
    }
}