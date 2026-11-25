package ma.ensam.campusconnect.controllers;

import ma.ensam.campusconnect.services.PublicationAffichageService;
import ma.ensam.campusconnect.services.TableauDeBordService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class HomeController {

    private final PublicationAffichageService publicationAffichageService;
    private final TableauDeBordService tableauDeBordService;

    public HomeController(PublicationAffichageService publicationAffichageService,
                          TableauDeBordService tableauDeBordService) {
        this.publicationAffichageService = publicationAffichageService;
        this.tableauDeBordService = tableauDeBordService;
    }

    @GetMapping("/")
    public String racine() {
        return "redirect:/home";
    }

    @GetMapping("/presentation")
    public String presentation(Model model) {
        model.addAttribute("clubs", List.of());
        return "auth/landing";
    }

    @GetMapping("/home")
    public String home(@RequestParam(required = false) String type,
                       Authentication auth, Model model) {
        String email = auth.getName();
        model.addAttribute("activePage", "home");
        model.addAttribute("feedType", (type == null || type.isBlank()) ? null : type);
        model.addAttribute("stats", tableauDeBordService.stats(email));
        model.addAttribute("posts", publicationAffichageService.accueil(email, type));
        model.addAttribute("recommendedClubs", tableauDeBordService.recommandes(email));
        model.addAttribute("memberships", tableauDeBordService.memberships(email));
        return "student/dashboard";
    }
}