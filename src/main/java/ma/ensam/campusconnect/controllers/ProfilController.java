package ma.ensam.campusconnect.controllers;

import ma.ensam.campusconnect.services.ProfilService;
import ma.ensam.campusconnect.services.TableauDeBordService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProfilController {

    private final ProfilService profilService;
    private final TableauDeBordService tableauDeBordService;

    public ProfilController(ProfilService profilService,
                            TableauDeBordService tableauDeBordService) {
        this.profilService = profilService;
        this.tableauDeBordService = tableauDeBordService;
    }

    @GetMapping("/profile")
    public String profile(Authentication auth, Model model) {
        String email = auth.getName();
        model.addAttribute("activePage", "profile");
        model.addAttribute("profile", profilService.profil(email));
        model.addAttribute("roleLabels", profilService.rolesLabels(email));
        model.addAttribute("followedClubs", tableauDeBordService.clubsSuivis(email));
        model.addAttribute("memberships", tableauDeBordService.memberships(email));
        model.addAttribute("interactions", List.of());   // branché à l'étape 3
        return "student/profile";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("activePage", "settings");
        return "student/settings";
    }

    @PostMapping("/settings/password")
    public String changerMotDePasse(@RequestParam String motDePasseActuel,
                                    @RequestParam String nouveauMotDePasse,
                                    @RequestParam String confirmation,
                                    Authentication auth,
                                    RedirectAttributes redirectAttributes) {
        try {
            profilService.changerMotDePasse(auth.getName(), motDePasseActuel,
                    nouveauMotDePasse, confirmation);
            redirectAttributes.addFlashAttribute("success", "Votre mot de passe a été modifié.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/settings";
    }
}