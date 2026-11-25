package ma.ensam.campusconnect.controllers;

import ma.ensam.campusconnect.enums.TypeRoleClub;
import ma.ensam.campusconnect.services.RoleClubService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/roles-club")
public class RoleClubController {

    private final RoleClubService roleClubService;

    public RoleClubController(RoleClubService roleClubService) {
        this.roleClubService = roleClubService;
    }

    @GetMapping
    public String afficherRoles(Model model) {
        model.addAttribute("roles", roleClubService.getAllRoles());
        return "roles-club/liste";
    }

    @GetMapping("/ajouter")
    public String afficherFormulaire(Model model) {
        model.addAttribute("etudiants", roleClubService.getEtudiants());
        model.addAttribute("clubs", roleClubService.getClubs());
        model.addAttribute("typesRoles", TypeRoleClub.values());

        return "roles-club/formulaire";
    }

    @PostMapping("/ajouter")
    public String ajouterRole(@RequestParam Long utilisateurId,
                              @RequestParam Long clubId,
                              @RequestParam TypeRoleClub role) {
        roleClubService.attribuerRole(utilisateurId, clubId, role);
        return "redirect:/roles-club";
    }

    @PostMapping("/supprimer/{id}")
    public String supprimerRole(@PathVariable Long id) {
        roleClubService.deleteRole(id);
        return "redirect:/roles-club";
    }
}