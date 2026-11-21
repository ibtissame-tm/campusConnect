package ma.ensam.campusconnect.controllers;

import ma.ensam.campusconnect.entities.Adhesion;
import ma.ensam.campusconnect.services.AdhesionService;
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
    public String afficherFormulaire(Model model) {
        model.addAttribute("adhesion", new Adhesion());
        return "adhesions/formulaire";
    }

    @PostMapping("/ajouter")
    public String ajouterAdhesion(@ModelAttribute Adhesion adhesion) {
        adhesionService.saveAdhesion(adhesion);
        return "redirect:/adhesions";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimerAdhesion(@PathVariable Long id) {
        adhesionService.deleteAdhesion(id);
        return "redirect:/adhesions";
    }
}