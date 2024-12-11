/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MySQL.Incidencia;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.service.MySQL.IncidenciaService;
import Projecte2.DriftoCar.service.MySQL.VehicleService;
import java.util.ArrayList;
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

    @GetMapping("/llistar-incidencies")
    public String llistarIncidencies(Model model) {
        List<Incidencia> incidencies = incidenciaService.llistarIncidencies();
        model.addAttribute("incidencies", incidencies);
        return "incidencia-llistar"; 
    }
    
    @GetMapping("/llistar")
    public String llistarVehicles(@RequestParam(value = "matricula", required = false) String matricula, Model model) {
        List<Vehicle> vehicles;

        if (matricula != null && !matricula.isEmpty()) {
            Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);
            if (vehicle != null) {
                vehicles = List.of(vehicle);
            } else {
                model.addAttribute("error", "No s'ha trobat el vehicle amb la matrícula: " + matricula);
                vehicles = new ArrayList<>();
            }
        } else {
            vehicles = vehicleService.llistarVehicles();
        }

        model.addAttribute("vehicles", vehicles);
        return "incidencia-cerca-vehicle";
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

    // Guardar la incidència
    @PostMapping("/obrir")
    public String guardarIncidencia(@ModelAttribute("incidencia") Incidencia incidencia, RedirectAttributes redirectAttributes) {
        try {
            incidenciaService.obrirIncidencia(incidencia); // Crear la incidència i actualitzar la disponibilitat del vehicle
            redirectAttributes.addFlashAttribute("success", "Incidència oberta correctament.");
            return "redirect:/incidencia/llistar"; // Redirigir després de guardar la incidència
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error en obrir la incidència: " + e.getMessage());
            return "redirect:/incidencia/llistar"; // Redirigir en cas d'error
        }
    }
}
