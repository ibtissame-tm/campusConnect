package ma.ensam.campusconnect.controllers;

import ma.ensam.campusconnect.services.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping
    public String dashboard(@RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("activePage", "admin");
        model.addAttribute("stats", adminService.stats());
        model.addAttribute("activity", adminService.activite());
        model.addAttribute("reports", List.of());          // branché à l'étape 3
        model.addAttribute("clubs", adminService.clubs());
        model.addAttribute("students", adminService.etudiants(q));
        model.addAttribute("q", q);
        return "admin/dashboard";
    }

    @PostMapping("/clubs")
    public String creerClub(@RequestParam String nom,
                            @RequestParam String description,
                            RedirectAttributes redirectAttributes) {
        try {
            adminService.creerClub(nom, description);
            redirectAttributes.addFlashAttribute("success", "Le club « " + nom.trim() + " » a été créé.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin";
    }
}