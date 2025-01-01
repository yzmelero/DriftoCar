/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.entity.MySQL.Reserva;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;
import Projecte2.DriftoCar.service.MySQL.ReservaService;
import Projecte2.DriftoCar.service.MySQL.VehicleService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Anna
 */
@Controller
@RequestMapping("/vehicle")
@Scope("session")
public class VehicleController {

    Logger log = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private LocalitzacioService localitzacioService;

    @Autowired
    private ReservaService reservaService;

    // Llistar 
    @GetMapping("/llistar")
    public String llistarVehicles(
            @RequestParam(value = "matricula", required = false) String matricula,
            @RequestParam(value = "dataInici", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInici,
            @RequestParam(value = "dataFinal", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFinal,
            Model model) {

        
        List<Vehicle> vehicles = vehicleService.findVehiclesLista(dataInici, dataFinal, matricula);
        model.addAttribute("matricula", matricula);
        model.addAttribute("dataInici", dataInici);
        model.addAttribute("dataFinal", dataFinal);
        model.addAttribute("vehicles", vehicles);
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
    public String guardarNouVehicle(@ModelAttribute("vehicle") Vehicle vehicle,
            @RequestParam("imatgeFile") MultipartFile imatgeFile) {

        try {
            if (!imatgeFile.isEmpty()) {
                // Convierte la imagen a un array de bytes
                vehicle.setImatge(imatgeFile.getBytes());
            }
            vehicleService.altaVehicle(vehicle);
            log.info("S'ha afegit un vehicle amb imatge en SQL.");
        } catch (IOException e) {
            log.error("Error al convertir la imatge: " + e.getMessage());
        }
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

        if (vehicle == null) {
            model.addAttribute("error", "No s'ha trobat el vehicle amb matrícula: " + matricula);
            return "redirect:/vehicle/llistar";
        }

        if (vehicle.getImatge() != null) {
            String imatgeBase64 = Base64.getEncoder().encodeToString(vehicle.getImatge());
            model.addAttribute("imatgeBase64", imatgeBase64);
        }
        model.addAttribute("vehicle", vehicle);
        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions);

        return "vehicle-modificar";
    }

    @PostMapping("/modificar")
    public String guardarVehicleModificat(@ModelAttribute("vehicle") Vehicle vehicle,
            @RequestParam("imatgeFile") MultipartFile imatgeFile) {
        try {
            if (!imatgeFile.isEmpty()) {
                vehicle.setImatge(imatgeFile.getBytes());
            } else {
                Vehicle vehicleAnterior = vehicleService.obtenirVehicleMatricula(vehicle.getMatricula());
                vehicle.setImatge(vehicleAnterior.getImatge());
            }
            Vehicle vehicleDisp = vehicleService.obtenirVehicleMatricula(vehicle.getMatricula());
            vehicle.setDisponibilitat(vehicleDisp.isDisponibilitat());
            vehicleService.modificaVehicle(vehicle);
            log.info("S'ha modificat el vehicle amb matrícula: " + vehicle.getMatricula());
        } catch (IOException e) {
            log.error("Error al carregar la imatge: " + e.getMessage());
        }
        return "redirect:/vehicle/llistar";

    }

    @GetMapping("/consulta/{matricula}")
    public String mostrarVehicle(@PathVariable String matricula, Model model) {
        Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);
        if (vehicle == null) {
            model.addAttribute("error", "No s'ha trobat vehicle amb matrícula: " + matricula);
            return "redirect:/vehicle/llistar";
        }
        if (vehicle.getImatge() != null) {
            String imatgeBase64 = Base64.getEncoder().encodeToString(vehicle.getImatge());
            model.addAttribute("imatgeBase64", imatgeBase64);
        } else {
            model.addAttribute("imatgeBase64", null);
        }
        model.addAttribute("vehicle", vehicle);
        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions);
        return "vehicle-consulta";
    }

    @GetMapping("/desactivar/{matricula}")
    public String desactivarVehicle(@PathVariable("matricula") String matricula,
            @RequestParam(value = "dataFinal", required = false) LocalDate dataFinal,
            Model model) {

        Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);
        if (vehicle == null) {
            model.addAttribute("error", "No hi ha cap vehicle amb matrícula: " + matricula);
            return "redirect:/vehicle/llistar";
        }

        List<Reserva> reserves = reservaService.obtenirReservesPerMatricula(matricula);

        if (dataFinal != null) {
            List<Reserva> reservasFiltrades = new ArrayList<>();
            for (Reserva reserva : reserves) {
                if (reserva.getDataInici().isBefore(dataFinal) || reserva.getDataInici().isEqual(dataFinal)) {
                    reservasFiltrades.add(reserva);
                }
            }
            reserves = reservasFiltrades;
        }
        model.addAttribute("vehicle", vehicle);
        model.addAttribute("reservas", reserves);

        return "vehicle-desactivar";
    }

    @PostMapping("/desactivarReserves")
    public String desactivarReserves(@RequestParam(value = "idReservas", required = false) List<Long> idReservas,
            @RequestParam("matricula") String matricula) {

        if (idReservas != null && !idReservas.isEmpty()) {
            for (Long id : idReservas) {
                reservaService.desactivarReserva(id);
            }
        }
        vehicleService.desactivarVehicle(matricula);

        return "redirect:/vehicle/llistar";
    }

    @GetMapping("/activar/{matricula}")
    public String activarVehicles(@PathVariable("matricula") String matricula, Model model) {
        Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);
        model.addAttribute("vehicle", vehicle);
        return "vehicle-activar";
    }

    @PostMapping("/activar/{matricula}")
    public String confirmarActivarVehicles(@PathVariable("matricula") String matricula,
            @RequestParam(value = "motiu", required = false) String motiu,
            @RequestParam(value = "importe", required = false) Double importe) {

        Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);

        vehicle.setDisponibilitat(true);
        if (motiu == null || motiu.isEmpty()) {
            vehicle.setMotiu(null);
        } else {
            vehicle.setMotiu(motiu);
        }
        if (importe == null) {
            vehicle.setImporte(0);
        } else {
            vehicle.setImporte(importe);
        }

        vehicleService.modificaVehicle(vehicle);
        return "redirect:/vehicle/llistar";
    }
}
