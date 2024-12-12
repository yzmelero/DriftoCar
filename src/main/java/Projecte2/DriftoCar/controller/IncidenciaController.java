/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MongoDB.DocumentacioIncidencia;
import Projecte2.DriftoCar.entity.MySQL.Incidencia;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.service.MongoDB.DocumentacioIncidenciaService;
import Projecte2.DriftoCar.service.MySQL.IncidenciaService;
import Projecte2.DriftoCar.service.MySQL.VehicleService;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Hector
 */
@Controller
@RequestMapping("/incidencia")
public class IncidenciaController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private IncidenciaService incidenciaService;
    
    @Autowired
    private DocumentacioIncidenciaService documentacioIncidenciaService;

    @GetMapping("/llistar-incidencies")
    public String llistarIncidencies(Model model) {
        List<Incidencia> incidencies = incidenciaService.llistarIncidencies();
        model.addAttribute("incidencies", incidencies);
        return "incidencia-llistar";
    }

    @GetMapping("/llistar")
    public String llistarVehiclesSenseIncidencies(Model model) {
        // Obtenim els vehicles sense incidències actives
        List<Vehicle> vehiclesSenseIncidencies = incidenciaService.llistarVehiclesSenseIncidenciesActives();

        // Afegim els vehicles al model
        model.addAttribute("vehicles", vehiclesSenseIncidencies);

        return "incidencia-cerca-vehicle"; // La vista que mostrarà la llista
    }

    // Mostrar el formulario de creación de la incidencia
    @GetMapping("/obrir/{matricula}")
    public String obrirIncidencia(@PathVariable String matricula, Model model) {
        // Obtenir el vehicle per matrícula
        Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);

        if (vehicle == null) {
            model.addAttribute("error", "El vehicle amb la matrícula " + matricula + " no existeix.");
            return "redirect:/incidencia/llistar"; // Redirigeix si el vehicle no es troba
        }

        // Crear una nova incidència per al vehicle
        Incidencia incidencia = new Incidencia();
        incidencia.setMatricula(vehicle);
        model.addAttribute("incidencia", incidencia);

        return "incidencia-alta"; // Mostrar el formulari per crear la incidència
    }

    @PostMapping("/obrir")
    public String guardarIncidencia(@ModelAttribute("incidencia") Incidencia incidencia,
            @RequestParam("fotos") MultipartFile[] fotos,
            @RequestParam("pdf") MultipartFile[] pdf,
            @RequestParam("text") String text, // Capturamos el texto adicional para la documentación
            RedirectAttributes redirectAttributes) {
        try {
            // Guardar la incidencia en MySQL
            incidenciaService.obrirIncidencia(incidencia);

            // Crear la documentación asociada a la incidencia para MongoDB
            DocumentacioIncidencia documentacio = new DocumentacioIncidencia();
            documentacio.setText(text);  // Establecer el texto redactado por el usuario
            documentacio.setFotos(documentacioIncidenciaService.convertirMultipartsABinary(fotos));  // Convertir fotos a Binary
            documentacio.setPdf(documentacioIncidenciaService.convertirMultipartsABinary(pdf));    // Convertir PDFs a Binary

            // Guardar la documentación en MongoDB
            documentacioIncidenciaService.guardarDocumentacio(documentacio);

            // Mensaje de éxito
            redirectAttributes.addFlashAttribute("success", "Incidència oberta correctament amb documentació.");
            return "redirect:/incidencia/llistar";  // Redirigir después de guardar
        } catch (RuntimeException | IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error en obrir la incidència: " + e.getMessage());
            return "redirect:/incidencia/llistar";  // Redirigir en caso de error
        }
    }

    @GetMapping("/tancar/{id}")
    public String tancarIncidencia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            incidenciaService.tancarIncidencia(id);
            redirectAttributes.addFlashAttribute("success", "Incidència tancada correctament.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error en tancar la incidència: " + e.getMessage());
        }
        return "redirect:/incidencia/llistar-incidencies";
    }
}
