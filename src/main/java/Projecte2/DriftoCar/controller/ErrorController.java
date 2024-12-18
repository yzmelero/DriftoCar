package Projecte2.DriftoCar.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

    @GetMapping("/errorPermisos")
    public String accessDenied(Model model) {
        model.addAttribute("error", "No tens permisos per accedir a aquesta pàgina.");
        return "errorPermisos";
    }
}