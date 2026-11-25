package ma.ensam.campusconnect.controllers;

import ma.ensam.campusconnect.dto.ClubView;
import ma.ensam.campusconnect.services.AbonnementService;
import ma.ensam.campusconnect.services.AdhesionService;
import ma.ensam.campusconnect.services.ClubAffichageService;
import ma.ensam.campusconnect.services.PublicationAffichageService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/clubs")
public class ClubController {

    private static final int TAILLE_PAGE = 9;

    private final ClubAffichageService clubAffichageService;
    private final AbonnementService abonnementService;
    private final AdhesionService adhesionService;
    private final PublicationAffichageService publicationAffichageService;

    public ClubController(ClubAffichageService clubAffichageService,
                          AbonnementService abonnementService,
                          AdhesionService adhesionService,
                          PublicationAffichageService publicationAffichageService) {
        this.clubAffichageService = clubAffichageService;
        this.abonnementService = abonnementService;
        this.adhesionService = adhesionService;
        this.publicationAffichageService = publicationAffichageService;
    }

    @GetMapping
    public String liste(@RequestParam(defaultValue = "") String q,
                        @RequestParam(defaultValue = "all") String filter,
                        @RequestParam(defaultValue = "popular") String sort,
                        @RequestParam(defaultValue = "1") int page,
                        Authentication auth, Model model) {

        List<ClubView> tous = clubAffichageService.lister(auth.getName(), q, filter, sort);

        int totalPages = Math.max(1, (int) Math.ceil(tous.size() / (double) TAILLE_PAGE));
        int pageCourante = Math.min(Math.max(page, 1), totalPages);
        int debut = (pageCourante - 1) * TAILLE_PAGE;
        int fin = Math.min(debut + TAILLE_PAGE, tous.size());

        model.addAttribute("clubs", tous.subList(debut, fin));
        model.addAttribute("q", q);
        model.addAttribute("filter", filter);
        model.addAttribute("sort", sort);
        model.addAttribute("page", pageCourante);
        model.addAttribute("totalPages", totalPages);
        return "student/clubs";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Authentication auth, Model model) {
        ClubView club = clubAffichageService.detail(auth.getName(), id);
        model.addAttribute("club", club);
        model.addAttribute("posts", publicationAffichageService.duClub(auth.getName(), id));
        model.addAttribute("isMember", "ACTIVE".equals(club.getMembershipStatus()));
        return "club/detail";
    }

    @PostMapping("/{id}/follow")
    public String suivre(@PathVariable Long id, Authentication auth,
                         @RequestHeader(value = "Referer", required = false) String referer) {
        abonnementService.suivre(auth.getName(), id);
        return retour(referer, id);
    }

    @PostMapping("/{id}/unfollow")
    public String nePlusSuivre(@PathVariable Long id, Authentication auth,
                               @RequestHeader(value = "Referer", required = false) String referer) {
        abonnementService.nePlusSuivre(auth.getName(), id);
        return retour(referer, id);
    }

    @PostMapping("/{id}/join")
    public String rejoindre(@PathVariable Long id, Authentication auth) {
        adhesionService.demanderAdhesion(auth.getName(), id);   // toujours EN_ATTENTE
        return "redirect:/clubs/" + id;
    }

    /** Revient sur la page club si on venait de là, sinon sur la liste. */
    private String retour(String referer, Long id) {
        String pageClub = "/clubs/" + id;
        return (referer != null && referer.endsWith(pageClub))
                ? "redirect:" + pageClub
                : "redirect:/clubs";
    }
}