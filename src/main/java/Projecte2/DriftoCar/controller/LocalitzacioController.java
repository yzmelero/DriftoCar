package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Anna
 */
@Controller
@RequestMapping("/localitzacions")
public class LocalitzacioController {

    @Autowired
    private LocalitzacioService localitzacioService;

    // Llistar totes les localitzacions
    @GetMapping("/llistar")
    public String llistarLocalitzacions(Model model) {
        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions);
        return "localitzacio-llista"; // Retorna la vista de Thymeleaf per llistar localitzacions
    }

}
