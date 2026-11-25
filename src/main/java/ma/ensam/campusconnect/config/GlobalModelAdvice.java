package ma.ensam.campusconnect.config;

import ma.ensam.campusconnect.dto.StudentView;
import ma.ensam.campusconnect.entities.Utilisateur;
import ma.ensam.campusconnect.enums.RoleGlobal;
import ma.ensam.campusconnect.repositories.RoleClubLectureRepository;
import ma.ensam.campusconnect.repositories.UtilisateurRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final UtilisateurRepository utilisateurRepository;
    private final RoleClubLectureRepository roleClubLectureRepository;

    public GlobalModelAdvice(UtilisateurRepository utilisateurRepository,
                             RoleClubLectureRepository roleClubLectureRepository) {
        this.utilisateurRepository = utilisateurRepository;
        this.roleClubLectureRepository = roleClubLectureRepository;
    }

    @ModelAttribute
    public void ajouterAttributsCommuns(Authentication auth, Model model) {
        if (auth == null || auth instanceof AnonymousAuthenticationToken) return;

        Utilisateur u = utilisateurRepository.findByEmail(auth.getName()).orElse(null);
        if (u == null) return;

        model.addAttribute("student", new StudentView(u));
        model.addAttribute("isAdmin", u.getRoleGlobal() == RoleGlobal.ADMIN);
        model.addAttribute("isClubManager",
                roleClubLectureRepository.existsByUtilisateurId(u.getId()));
    }
}