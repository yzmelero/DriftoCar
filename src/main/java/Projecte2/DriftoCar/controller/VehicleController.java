/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;
import Projecte2.DriftoCar.service.MySQL.VehicleService;
import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Anna
 */
@Controller
@RequestMapping("/vehicle")
public class VehicleController {

    Logger log = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private LocalitzacioService localitzacioService;

    // Llistar 
    @GetMapping("/llistar")
    public String llistarVehicles(
            @RequestParam(value = "dataInici", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInici,
            @RequestParam(value = "dataFinal", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFinal,
            Model model) {
        List<Vehicle> vehicles = vehicleService.llistarVehicles();
        model.addAttribute("vehicles", vehicles);
        if (dataInici != null && dataFinal != null) {
            List<Vehicle> vehiclesDisponibles = vehicleService.getVehiclesDisponibles(dataInici, dataFinal);
            model.addAttribute("vehicles", vehiclesDisponibles);
        }

        return "vehicle-llistar";
    }

    // Alta
    @GetMapping("/afegir")
    public String afegirVehicles(Model model) {
        model.addAttribute("vehicles", new Vehicle());
        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions);
        return "vehicle-alta";
    }

    @PostMapping("/afegir")
    public String guardarNouVehicle(@ModelAttribute("vehicle") Vehicle vehicle) {
        vehicleService.altaVehicle(vehicle);
        log.info("S'ha entrat al metode d'altaReserva.");
        return "redirect:/vehicle/llistar";
    }

    // Esborrar
    @GetMapping("/esborrar/{matricula}")
    public String esborrarVehicle(@PathVariable("matricula") String matricula) {
        vehicleService.baixaVehicle(matricula);
        log.info("S'ha esborrat un vehicle.");

        return "redirect:/vehicle/llistar";
    }

    // Modifica
    @GetMapping("/modificar/{matricula}")
    public String modificarVehicle(@PathVariable("matricula") String matricula, Model model) {
        Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);
        model.addAttribute("vehicle", vehicle);
        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions);
        return "vehicle-modificar";
    }

    @PostMapping("/modificar")
    public String guardarVehicleModificat(@ModelAttribute("vehicle") Vehicle vehicle) {
        vehicleService.modificaVehicle(vehicle);
        return "redirect:/vehicle/llistar";
    }

    @GetMapping("/detall/{matricula}")
    public String mostrarVehicle(@PathVariable String matricula, Model model) {
        Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);
        if (vehicle == null) {
            model.addAttribute("error", "No s'ha trobat vehicle amb matrícula: " + matricula);
            return "redirect:/vehicle/llistar";
        }
        model.addAttribute("vehicle", vehicle);
        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions);
        return "vehicle-detalls";
    }
}
