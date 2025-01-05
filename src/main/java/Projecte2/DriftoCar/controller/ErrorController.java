package Projecte2.DriftoCar.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador per gestionar errors relacionats amb l'accés denegat.
 */
@Controller
public class ErrorController {

    private static final Logger log = LoggerFactory.getLogger(ErrorController.class);

    /**
     * Mostra una pàgina d'error quan l'usuari no té permisos per accedir a una pàgina.
     *
     * @param model El model que es passarà a la vista.
     * @return El nom de la vista d'error d'accés.
     */
    @GetMapping("/errorPermisos")
    public String accessDenied(Model model) {
        log.warn("Intent d'accés a una pàgina sense permisos.");
        model.addAttribute("error", "No tens permisos per accedir a aquesta pàgina.");
        return "errorPermisos";
    }
}