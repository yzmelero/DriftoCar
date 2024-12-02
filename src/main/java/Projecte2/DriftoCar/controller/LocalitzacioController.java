package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Anna
 */
@Controller
@RequestMapping("/localitzacio")
public class LocalitzacioController {

    @Autowired
    private LocalitzacioService localitzacioService;

    @GetMapping("/llistar")
    public String llistarLocalitzacions(Model model) {
        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions);
        return "localitzacio-llistar";
    }

    @GetMapping("/alta")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("localitzacio", new Localitzacio());
        return "localitzacio-alta";  // Retorna la vista de Thymeleaf para crear una nueva localización
    }

    @PostMapping("/alta")
    public String altaLocalitzacio(@ModelAttribute("localitzacio") Localitzacio localitzacio) {
        try {
            localitzacioService.altaLocalitzacio(localitzacio);
            return "redirect:/localitzacio/llistar";
        } catch (RuntimeException e) {
            return "error";
        }
    }
}
