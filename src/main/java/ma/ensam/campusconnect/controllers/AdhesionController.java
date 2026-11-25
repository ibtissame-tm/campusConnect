package ma.ensam.campusconnect.controllers;

import ma.ensam.campusconnect.services.AdhesionService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/adhesions")
public class AdhesionController {

    private final AdhesionService adhesionService;

    public AdhesionController(AdhesionService adhesionService) {
        this.adhesionService = adhesionService;
    }

    @GetMapping
    public String afficherAdhesions(Model model) {
        model.addAttribute("adhesions", adhesionService.getAllAdhesions());
        return "adhesions/liste";
    }

    @GetMapping("/ajouter")
    public String afficherFormulaire() {
        return "adhesions/formulaire";
    }

    @PostMapping("/demander")
    public String demanderAdhesion(@RequestParam Long clubId, Authentication authentication) {
        adhesionService.demanderAdhesion(authentication.getName(), clubId);
        return "redirect:/adhesions";
    }

    @PostMapping("/{id}/valider")
    public String valider(@PathVariable Long id, Authentication authentication) {
        adhesionService.valider(id, authentication.getName());
        return "redirect:/adhesions";
    }

    @PostMapping("/{id}/refuser")
    public String refuser(@PathVariable Long id, Authentication authentication) {
        adhesionService.refuser(id, authentication.getName());
        return "redirect:/adhesions";
    }

    @PostMapping("/supprimer/{id}")
    public String supprimerAdhesion(@PathVariable Long id, Authentication authentication) {
        adhesionService.supprimer(id, authentication.getName());
        return "redirect:/adhesions";
    }
}