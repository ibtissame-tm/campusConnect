package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.entities.Club;
import ma.ensam.campusconnect.entities.RoleClub;
import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.TypeRoleClub;
import ma.ensam.campusconnect.repositories.ClubRepository;
import ma.ensam.campusconnect.repositories.RoleClubRepository;
import ma.ensam.campusconnect.repositories.UtilisateurRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleClubService {

    private final RoleClubRepository roleClubRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ClubRepository clubRepository;

    public RoleClubService(RoleClubRepository roleClubRepository,
                           UtilisateurRepository utilisateurRepository,
                           ClubRepository clubRepository) {
        this.roleClubRepository = roleClubRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.clubRepository = clubRepository;
    }

    public List<RoleClub> getAllRoles() {
        return roleClubRepository.findAll();
    }
    public List<Utilisateur> getEtudiants() {
        return utilisateurRepository.findAll()
                .stream()
                .filter(u -> u.getRoleGlobal() == ma.ensam.campusconnect.enums.RoleGlobal.ETUDIANT)
                .toList();
    }

    public List<Club> getClubs() {
        return clubRepository.findAll();
    }

    public RoleClub getRoleById(Long id) {
        return roleClubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rôle introuvable"));
    }

    @Transactional
    public RoleClub attribuerRole(Long utilisateurId, Long clubId, TypeRoleClub role) {

        // Vérifier si l'utilisateur possède déjà ce rôle dans ce club
        if (roleClubRepository.existsByUtilisateurIdAndClubIdAndRole(
                utilisateurId, clubId, role)) {
            throw new IllegalArgumentException(
                    "Cet utilisateur possède déjà ce rôle dans ce club."
            );
        }

        // Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur introuvable"));

        // Récupérer le club
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new RuntimeException("Club introuvable"));

        // Créer l'attribution du rôle
        RoleClub roleClub = new RoleClub();
        roleClub.setUtilisateur(utilisateur);
        roleClub.setClub(club);
        roleClub.setRole(role);

        return roleClubRepository.save(roleClub);
    }

    public void deleteRole(Long id) {
        roleClubRepository.deleteById(id);
    }
}