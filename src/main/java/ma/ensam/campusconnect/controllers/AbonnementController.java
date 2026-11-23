package ma.ensam.campusconnect.controllers;

import ma.ensam.campusconnect.entities.Abonnement;
import ma.ensam.campusconnect.services.AbonnementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/abonnements")
public class AbonnementController {

    private final AbonnementService abonnementService;

    public AbonnementController(AbonnementService abonnementService) {
        this.abonnementService = abonnementService;
    }

    @GetMapping
    public String afficherAbonnements(Model model) {
        model.addAttribute("abonnements", abonnementService.getAllAbonnements());
        return "abonnements/liste";
    }

    @GetMapping("/ajouter")
    public String afficherFormulaire(Model model) {
        model.addAttribute("abonnement", new Abonnement());
        return "abonnements/formulaire";
    }

    @PostMapping("/ajouter")
    public String ajouterAbonnement(@ModelAttribute Abonnement abonnement) {
        abonnementService.saveAbonnement(abonnement);
        return "redirect:/abonnements";
    }

    @GetMapping("/supprimer/{id}")
    public String supprimerAbonnement(@PathVariable Long id) {
        abonnementService.deleteAbonnement(id);
        return "redirect:/abonnements";
    }
}