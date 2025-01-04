package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador per gestionar les operacions relacionades amb les localitzacions.
 * 
 * Aquesta classe proporciona funcionalitats per:
 * - Llistar totes les localitzacions.
 * - Mostrar els formularis d'alta, modificació i consulta d'una localització.
 * - Donar d'alta, modificar i esborrar localitzacions en la base de dades.
 * 
 * Els mètodes inclouen la gestió d'errors, validació de dades i interacció amb el servei de localitzacions per realitzar les operacions.
 * 
 * Dependències:
 * - {@code LocalitzacioService}: Per gestionar la lògica de negoci de les localitzacions.
 * 
 * Els logs es registren per seguir el flux de les operacions i facilitar la depuració d'errors.
 */
@Controller
@RequestMapping("/localitzacio")
@Scope("session")
public class LocalitzacioController {

    Logger log = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    private LocalitzacioService localitzacioService;

    /**
     * Mètode per llistar totes les localitzacions.
     * 
     * Afegeix la llista de localitzacions al model perquè es pugui visualitzar a la vista.
     * 
     * @param model El model on s'afegiran les localitzacions.
     * @return El nom de la vista per llistar les localitzacions.
     */
    @GetMapping("/llistar")
    public String llistarLocalitzacions(Model model) {
        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions);
        log.info("Llista de localitzacions obtinguda correctament.");
        return "localitzacio-llistar";
    }

    /**
     * Mètode per mostrar el formulari d'alta d'una nova localització.
     * 
     * Afegeix una nova instància de {@link Localitzacio} al model per mostrar el formulari
     * a l'usuari.
     * 
     * @param model El model per a la vista del formulari d'alta.
     * @return El nom de la vista per mostrar el formulari d'alta.
     */
    @GetMapping("/alta")
    public String mostrarFormularioAlta(Model model) {
        model.addAttribute("localitzacio", new Localitzacio());
        log.info("Mostrant formulari d'alta de localització.");
        return "localitzacio-alta";
    }

    /**
     * Mètode per alta d'una nova localització.
     * 
     * Aquest mètode processa el formulari per donar d'alta una nova localització i guarda
     * la informació. També registra l'horari d'obertura i tancament.
     * 
     * @param localitzacio Objecte {@link Localitzacio} amb les dades enviades pel formulari.
     * @param model Model per afegir missatges d'error o èxit.
     * @param horaObertura Hora d'obertura de la localització.
     * @param horaTancament Hora de tancament de la localització.
     * @return Redirigeix a la vista de llistat de localitzacions.
     */
    @PostMapping("/alta")
    public String altaLocalitzacio(@ModelAttribute("localitzacio") Localitzacio localitzacio, Model model,
            @RequestParam("horaObertura") String horaObertura,
            @RequestParam("horaTancament") String horaTancament) {
        
        log.info("Entrant al mètode altaLocalitzacio controller.");

        try {
            localitzacio.setHorari(horaObertura + " - " + horaTancament);

            localitzacioService.altaLocalitzacio(localitzacio);

            log.info("S'ha donat d'alta una localització amb codi postal: {}", localitzacio.getCodiPostal());
            return "redirect:/localitzacio/llistar";
        } catch (RuntimeException e) {
            log.error("Error en donar d'alta la localització amb codi postal: {}. Error: {}", localitzacio.getCodiPostal(), e.getMessage());
            model.addAttribute("error", "Ja existeix una localització amb aquest codi postal.");
            return "localitzacio-alta";
        }
    }

    /**
     * Mètode per esborrar una localització per codi postal.
     * 
     * Elimina la localització associada al codi postal passat com a paràmetre.
     * 
     * @param codiPostal Codi postal de la localització a eliminar.
     * @param redirectAttributes Atributs per redirigir i mostrar missatges d'èxit o error.
     * @return Redirigeix a la vista de llistat de localitzacions.
     */
    @GetMapping("/esborrar/{codiPostal}")
    public String esborrarLocalitzacio(@PathVariable String codiPostal, RedirectAttributes redirectAttributes) {
        try {
            localitzacioService.baixaLocalitzacio(codiPostal);
            log.info("S'ha esborrat la localització amb codi postal: {}", codiPostal);
            redirectAttributes.addFlashAttribute("success", "Localització eliminada correctament.");
        } catch (RuntimeException e) {
            log.error("Error en esborrar la localització amb codi postal: {}. Error: {}", codiPostal, e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/localitzacio/llistar";
    }

    /**
     * Mètode per mostrar el formulari per modificar una localització existent.
     * 
     * Obté la localització a modificar pel seu codi postal i la passa al model per
     * mostrar-la al formulari.
     * 
     * @param codiPostal Codi postal de la localització a modificar.
     * @param model El model per afegir la localització a modificar.
     * @return El nom de la vista per modificar la localització.
     */
    @GetMapping("/modificar/{codiPostal}")
    public String mostrarFormularioModificar(@PathVariable String codiPostal, Model model) {
        Localitzacio localitzacio = localitzacioService.obtenirLocalitzacioCodiPostal(codiPostal);
        if (localitzacio == null) {
            log.warn("No s'ha trobat cap localització amb el codi postal: {}", codiPostal);
            model.addAttribute("error", "No s'ha trobat cap localitzacio amb el codi postal: " + codiPostal);
            return "redirect:/localitzacio/llistar";
        }
        model.addAttribute("localitzacio", localitzacio);
        log.info("Mostrant formulari per modificar la localització amb codi postal: {}", codiPostal);
        return "localitzacio-modificar";
    }


    /**
     * Mètode per modificar una localització existent.
     * 
     * Actualitza una localització amb les dades proporcionades pel formulari.
     * 
     * @param localitzacio Objecte {@link Localitzacio} amb les dades a modificar.
     * @param horaObertura Hora d'obertura de la localització.
     * @param horaTancament Hora de tancament de la localització.
     * @return Redirigeix a la vista de llistat de localitzacions.
     */
    @PostMapping("/modificar")
    public String modificarLocalitzacio(@ModelAttribute("localitzacio") Localitzacio localitzacio,
            @RequestParam("horaObertura") String horaObertura,
            @RequestParam("horaTancament") String horaTancament) {
        try {
            localitzacio.setHorari(horaObertura + " - " + horaTancament);

            localitzacioService.modificarLocalitzacio(localitzacio.getCodiPostal(), localitzacio);
            
            log.info("S'ha modificat la localització amb codi postal: {}", localitzacio.getCodiPostal());
            return "redirect:/localitzacio/llistar";
        } catch (RuntimeException e) {
            log.error("Error en modificar la localització amb codi postal: {}. Error: {}", localitzacio.getCodiPostal(), e.getMessage());
            return "error";
        }
    }

    /**
     * Mètode per consultar els detalls d'una localització per codi postal.
     * 
     * Obté la localització associada al codi postal i la mostra al model.
     * 
     * @param codiPostal Codi postal de la localització a consultar.
     * @param model El model per afegir la localització consultada.
     * @return El nom de la vista per mostrar els detalls de la localització.
     */
    @GetMapping("/consulta/{codiPostal}")
    public String consultarLocalitzacio(@PathVariable String codiPostal, Model model) {
        Localitzacio localitzacio = localitzacioService.obtenirLocalitzacioCodiPostal(codiPostal);

        if (localitzacio == null) {
            log.warn("No s'ha trobat cap localització amb el codi postal: {}", codiPostal);
            model.addAttribute("error", "No s'ha trobat la localització amb el codi postal: " + codiPostal);
            return "redirect:/localitzacio/llistar";
        }

        model.addAttribute("localitzacio", localitzacio);
        log.info("S'ha obtingut la localització amb codi postal: {}", codiPostal);
        return "localitzacio-consulta";
    }

}
