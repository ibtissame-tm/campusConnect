package ma.ensam.campusconnect.services;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import ma.ensam.campusconnect.entities.Club;
import ma.ensam.campusconnect.entities.Publication;
import ma.ensam.campusconnect.entities.RoleClub;
import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.RoleGlobal;
import ma.ensam.campusconnect.enums.TypePublication;
import ma.ensam.campusconnect.enums.Visibilite;
import ma.ensam.campusconnect.repositories.ClubRepository;
import ma.ensam.campusconnect.repositories.PublicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service

public class PublicationService {
    @Autowired
    private PublicationRepository publicationRepository;
    @Autowired
    private ClubService clubService;
    @Transactional
    public Publication ajoueterPublication(Long clubId, String contenu, String imageUrl,Visibilite visibilite, TypePublication
            typePublication,Utilisateur auteur) {
        Publication publication = new Publication();
        publication.setContenu(contenu);
        publication.setImageUrl(imageUrl);
        publication.setTypePublication(typePublication!=null ? typePublication : TypePublication.ACTUALITE);
        publication.setAuteur(auteur);
        publication.setDateCreation(LocalDateTime.now());
        if(clubId==null){
            if(auteur.getRoleGlobal()!= RoleGlobal.ADMIN){
                throw new RuntimeException("Reserve a l'administrateur");
            }
            publication.setVisibilite(Visibilite.PUBLIQUE);

        }else{
            Club club=clubService.trouverParId(clubId);
            publication.setClub(club);
            publication.setVisibilite(visibilite);
        }
        return publicationRepository.save(publication);

    }



    public List<Publication> findAllPublications(Club club,Utilisateur demandeur) {
        if(demandeur.)
    }
    public Publication trouverParId(long id){
        return publicationRepository.findById(id).orElseThrow(() -> new RuntimeException("La publication n'existe pas"));
    }

    public void modifierPublication(Long id, String contenu, Visibilite visibilite ){
        Publication publication = trouverParId(id);
        publication.setContenu(contenu);
        publication.setVisibilite(visibilite);
        publication.setDateModification(LocalDateTime.now());
        publicationRepository.save(publication);
    }
    public void supprimerPublication(Long id){
        Publication publication = trouverParId(id);
        publicationRepository.delete(publication);
    }
}
