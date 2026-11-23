package ma.ensam.campusconnect.services;

import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.repositories.RoleClubLectureRepository;
import ma.ensam.campusconnect.repositories.UtilisateurRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service

public class UtilisateurDetailsService implements UserDetailsService {
    private final UtilisateurRepository utilisateurRepository;
    public UtilisateurDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }
    @Override
    public UserDetails loadUserByUsername(String email){
        Utilisateur utilisateur=utilisateurRepository.findByEmail(email.trim().toLowerCase()).
                orElseThrow(()->new UsernameNotFoundException("utilisateur introuvable"));
        return User.withUsername(utilisateur.getEmail()).password(utilisateur.getMotDePasse())
                .roles(utilisateur.getRoleGlobal().name()).disabled(!utilisateur.isActive())                // compte désactivé = connexion refusée
                .build();

    }


}
