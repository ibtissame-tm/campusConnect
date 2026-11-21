package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.entities.Club;
import ma.ensam.campusconnect.repositories.ClubRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service

public class ClubService {
    @Autowired
    private ClubRepository clubRepository;
    @Autowired
    private ThreadPoolTaskExecutor threadPoolTaskExecutor;

    public void creerClub(Club club) {
        if(clubRepository.existsByName(club.getNom())){
            throw new RuntimeException("Un clube avec ce nom existe deja");
        }
        club.setActive(true);
        club.setDateCreation(LocalDateTime.now());
        clubRepository.save(club);


    }
    public Club trouverParId(Long id) {
        return clubRepository.findById(id).orElseThrow(()-> new RuntimeException("Le club n'existe pas"));
    }
    public Club modifierClub(Long id, String nom,String description,String logoUrl,String couvertureUrl){
        Club club = trouverParId(id);
        club.setNom(nom);
        club.setDescription(description);
        club.setLogoUrl(logoUrl);
        club.setCouvertureUrl(couvertureUrl);
        club.setDateModification(LocalDateTime.now());
        return clubRepository.save(club);
    }
    public List<Club> obtenirTousLesClubs() {
        return clubRepository.findAll();

    }
    public void supprimerClub(Long id){
        Club club = trouverParId(id);
        clubRepository.delete(club);
    }
    public void activerClub(Long id){
        Club club = trouverParId(id);
        if(club.isActive()){
            throw new RuntimeException("Le club est deja active");
        }
        club.setActive(true);
        clubRepository.save(club);
    }
    public void disactiverClub(Long id){
        Club club = trouverParId(id);
        if(!club.isActive()){
            throw new RuntimeException("Le club est deja disactive");
        }
        club.setActive(false);
        clubRepository.save(club);
    }




}
