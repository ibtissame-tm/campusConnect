package ma.ensam.campusconnect.controllers;

import ma.ensam.campusconnect.services.PublicationAffichageService;
import ma.ensam.campusconnect.services.TableauDeBordService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class FeedController {

    private final PublicationAffichageService publicationAffichageService;
    private final TableauDeBordService tableauDeBordService;

    public FeedController(PublicationAffichageService publicationAffichageService,
                          TableauDeBordService tableauDeBordService) {
        this.publicationAffichageService = publicationAffichageService;
        this.tableauDeBordService = tableauDeBordService;
    }

    private static String normaliser(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    @GetMapping("/community")
    public String community(@RequestParam(required = false) String type,
                            @RequestParam(required = false) String visibility,
                            Authentication auth, Model model) {
        String email = auth.getName();
        model.addAttribute("activePage", "community");
        model.addAttribute("feedType", normaliser(type));
        model.addAttribute("feedVisibility", normaliser(visibility));
        model.addAttribute("posts", publicationAffichageService.communaute(email, type, visibility));
        model.addAttribute("followedClubs", tableauDeBordService.clubsSuivis(email));
        return "student/community";
    }

    @GetMapping("/competitions")
    public String competitions(@RequestParam(defaultValue = "all") String scope,
                               Authentication auth, Model model) {
        model.addAttribute("activePage", "competitions");
        model.addAttribute("scope", scope);
        model.addAttribute("competitions",
                publicationAffichageService.competitions(auth.getName(), scope));
        return "student/competitions";
    }
}