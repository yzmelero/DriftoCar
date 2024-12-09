package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Anna
 */
@Controller
@RequestMapping("/localitzacio")
public class LocalitzacioController {

    Logger log = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    private LocalitzacioService localitzacioService;

    //Llista
    @GetMapping("/llistar")
    public String llistarLocalitzacions(Model model) {
        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions);
        return "localitzacio-llistar";
    }

    //Alta
    @GetMapping("/alta")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("localitzacio", new Localitzacio());
        return "localitzacio-alta";
    }

    @PostMapping("/alta")
    public String altaLocalitzacio(@ModelAttribute("localitzacio") Localitzacio localitzacio, Model model) {
        try {
            localitzacioService.altaLocalitzacio(localitzacio);
            log.info("S'ha donat d'alta una localització..");
            return "redirect:/localitzacio/llistar";
        } catch (RuntimeException e) {
            model.addAttribute("error", "Ja existeix una localització amb aquest codi postal.");
            return "localitzacio-alta";
        }
    }

    //Esborrar
    @GetMapping("/esborrar/{codiPostal}")
    public String esborrarLocalitzacio(@PathVariable String codiPostal, RedirectAttributes redirectAttributes) {
        try {
            localitzacioService.baixaLocalitzacio(codiPostal);
            log.info("S'ha esborrat una localització.");
            redirectAttributes.addFlashAttribute("success", "Localització eliminada correctament.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/localitzacio/llistar";
    }
    
    //Modificar
    @GetMapping("/modificar/{codiPostal}")
    public String mostrarFormularioModificar(@PathVariable String codiPostal, Model model) {
        Localitzacio localitzacio = localitzacioService.obtenirLocalitzacioCodiPostal(codiPostal);
        if (localitzacio == null) {
            model.addAttribute("error", "No s'ha trobat cap localitzacio amb el codi postal: " + codiPostal);
            return "redirect:/localitzacio/llistar";
        }
        model.addAttribute("localitzacio", localitzacio);
        return "localitzacio-modificar";
    }

    @PostMapping("/modificar")
    public String modificarLocalitzacio(@ModelAttribute("localitzacio") Localitzacio localitzacio) {
        try {
            localitzacioService.modificarLocalitzacio(localitzacio.getCodiPostal(), localitzacio);
            log.info("S'ha modificat una localització..");
            return "redirect:/localitzacio/llistar";
        } catch (RuntimeException e) {
            return "error";
        }
    }

    //Consultar
    @GetMapping("/consulta/{codiPostal}")
    public String consultarLocalitzacio(@PathVariable String codiPostal, Model model) {
        Localitzacio localitzacio = localitzacioService.obtenirLocalitzacioCodiPostal(codiPostal);

        if (localitzacio == null) {
            model.addAttribute("error", "No s'ha trobat la localització amb el codi postal: " + codiPostal);
            return "redirect:/localitzacio/llistar";
        }

        model.addAttribute("localitzacio", localitzacio);
        return "localitzacio-consulta";
    }

}
