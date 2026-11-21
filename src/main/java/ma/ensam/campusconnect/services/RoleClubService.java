package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.entities.RoleClub;
import ma.ensam.campusconnect.repositories.RoleClubRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RoleClubService {

    private final RoleClubRepository roleClubRepository;

    public RoleClubService(RoleClubRepository roleClubRepository) {
        this.roleClubRepository = roleClubRepository;
    }

    public List<RoleClub> getAllRoles() {
        return roleClubRepository.findAll();
    }

    public RoleClub getRoleById(Long id) {
        return roleClubRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rôle introuvable"));
    }

    public RoleClub saveRole(RoleClub roleClub) {
        return roleClubRepository.save(roleClub);
    }

    public void deleteRole(Long id) {
        roleClubRepository.deleteById(id);
    }
}