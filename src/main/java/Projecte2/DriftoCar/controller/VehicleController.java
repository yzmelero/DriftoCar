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
 * Controlador per gestionar les operacions relacionades amb els vehicles.
 * Inclou funcions per llistar, afegir, modificar, esborrar, desactivar i
 * activar vehicles.
 * També permet la consulta detallada d'un vehicle.
 *
 * Gestiona les sol·licituds HTTP relacionades amb els vehicles.
 * 
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

    /**
     * Llista els vehicles disponibles segons els criteris proporcionats.
     * 
     * @param matricula Matrícula del vehicle per filtrar (opcional).
     * @param dataInici Data d'inici del període de disponibilitat (opcional).
     * @param dataFinal Data final del període de disponibilitat (opcional).
     * @param model     Model per passar dades a la vista.
     * @return Vista amb la llista de vehicles.
     */
    @GetMapping("/llistar")
    public String llistarVehicles(
            @RequestParam(value = "matricula", required = false) String matricula,
            @RequestParam(value = "dataInici", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataInici,
            @RequestParam(value = "dataFinal", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate dataFinal,
            Model model) {

        log.info("Iniciant llistat de vehicles amb matrícula: {}, data d'inici: {}, data final: {}.",
                matricula, dataInici, dataFinal);
        List<Vehicle> vehicles = vehicleService.findVehiclesLista(dataInici, dataFinal, matricula);
        log.info("S'han trobat {} vehicles que compleixen els criteris.", vehicles.size());

        model.addAttribute("matricula", matricula);
        model.addAttribute("dataInici", dataInici);
        model.addAttribute("dataFinal", dataFinal);
        model.addAttribute("vehicles", vehicles);
        return "vehicle-llistar";
    }

    /**
     * Mostra el formulari per afegir un nou vehicle.
     *
     * @param model Model per passar dades a la vista.
     * @return Vista del formulari d'alta de vehicles.
     */
    @GetMapping("/afegir")
    public String afegirVehicles(Model model) {
        log.info("Carregant formulari d'alta de vehicles.");

        model.addAttribute("vehicles", new Vehicle());
        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions);
        return "vehicle-alta";
    }

    /**
     * Guarda un nou vehicle a la base de dades.
     *
     * @param vehicle    Vehicle a afegir.
     * @param imatgeFile Fitxer d'imatge del vehicle (opcional).
     * @return Redirecció a la vista de llistat de vehicles.
     */
    @PostMapping("/afegir")
    public String guardarNouVehicle(@ModelAttribute("vehicle") Vehicle vehicle,
            @RequestParam("imatgeFile") MultipartFile imatgeFile, Model model) {

        log.info("Iniciant procés per guardar un nou vehicle amb matrícula: {}", vehicle.getMatricula());

        try {
            if (!imatgeFile.isEmpty()) {
                // Convierte la imagen a un array de bytes
                vehicle.setImatge(imatgeFile.getBytes());
                log.info("S'ha associat una imatge al vehicle.");
            }
            vehicleService.altaVehicle(vehicle);
            log.info("S'ha afegit correctament el vehicle amb matrícula: {}", vehicle.getMatricula());
        } catch (Exception e) {
            model.addAttribute("vehicle", vehicle);
            model.addAttribute("error", e.getMessage());
            log.error("Error en alta de vehicle" + e.getMessage());
            return "vehicle-alta";
        }
        return "redirect:/vehicle/llistar";
    }

    /**
     * Esborra un vehicle segons la seva matrícula.
     *
     * @param matricula Matrícula del vehicle a esborrar.
     * @return Redirecció a la vista de llistat de vehicles.
     */
    @GetMapping("/esborrar/{matricula}")
    public String esborrarVehicle(@PathVariable("matricula") String matricula) {
        log.info("Iniciant procés d'esborrat per al vehicle amb matrícula: {}", matricula);

        vehicleService.baixaVehicle(matricula);
        log.info("Vehicle amb matrícula {} esborrat correctament.", matricula);

        return "redirect:/vehicle/llistar";
    }

    /**
     * Mostra el formulari per modificar un vehicle.
     *
     * @param matricula Matrícula del vehicle a modificar.
     * @param model     Model per passar dades a la vista.
     * @return Vista del formulari de modificació.
     */
    @GetMapping("/modificar/{matricula}")
    public String modificarVehicle(@PathVariable("matricula") String matricula, Model model) {
        log.info("Carregant formulari de modificació per al vehicle amb matrícula: {}", matricula);

        Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);

        if (vehicle == null) {
            log.warn("No s'ha trobat cap vehicle amb matrícula: {}", matricula);
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

        log.info("Formulari carregat correctament per al vehicle amb matrícula: {}", matricula);
        return "vehicle-modificar";
    }

    /**
     * Guarda les modificacions realitzades a un vehicle existent.
     *
     * @param vehicle    Vehicle amb les dades modificades.
     * @param imatgeFile Fitxer d'imatge del vehicle (opcional).
     * @return Redirecció a la vista de llistat de vehicles.
     */
    @PostMapping("/modificar")
    public String guardarVehicleModificat(@ModelAttribute("vehicle") Vehicle vehicle,
            @RequestParam("imatgeFile") MultipartFile imatgeFile) {

        log.info("Iniciant procés per modificar el vehicle amb matrícula: {}", vehicle.getMatricula());

        try {
            if (!imatgeFile.isEmpty()) {
                vehicle.setImatge(imatgeFile.getBytes());
                log.info("S'ha actualitzat la imatge del vehicle.");
            } else {
                Vehicle vehicleAnterior = vehicleService.obtenirVehicleMatricula(vehicle.getMatricula());
                vehicle.setImatge(vehicleAnterior.getImatge());
                log.info("El vehicle conserva la imatge anterior.");

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

    /**
     * Mostra els detalls d'un vehicle especificat per la seva matrícula.
     *
     * @param matricula Matrícula del vehicle a consultar.
     * @param model     Model per passar dades a la vista.
     * @return Vista amb els detalls del vehicle.
     */
    @GetMapping("/consulta/{matricula}")
    public String mostrarVehicle(@PathVariable String matricula, Model model) {
        log.info("Iniciant consulta per al vehicle amb matrícula: {}", matricula);

        Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);
        if (vehicle == null) {
            log.warn("No s'ha trobat cap vehicle amb matrícula: {}", matricula);
            model.addAttribute("error", "No s'ha trobat vehicle amb matrícula: " + matricula);
            return "redirect:/vehicle/llistar";
        }
        if (vehicle.getImatge() != null) {
            String imatgeBase64 = Base64.getEncoder().encodeToString(vehicle.getImatge());
            model.addAttribute("imatgeBase64", imatgeBase64);
            log.info("S'ha carregat la imatge del vehicle.");
        } else {
            model.addAttribute("imatgeBase64", null);
            log.info("El vehicle no té cap imatge associada.");
        }
        model.addAttribute("vehicle", vehicle);
        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions);
        log.info("Consulta finalitzada correctament per al vehicle amb matrícula: {}", matricula);

        return "vehicle-consulta";
    }

    /**
     * Mostra una vista per desactivar un vehicle i les seves reserves associades.
     *
     * @param matricula Matrícula del vehicle a desactivar.
     * @param dataFinal Data màxima de reserves a considerar (opcional).
     * @param model     Model per passar dades a la vista.
     * @return Vista de desactivació de vehicle i reserves.
     */
    @GetMapping("/desactivar/{matricula}")
    public String desactivarVehicle(@PathVariable("matricula") String matricula,
            @RequestParam(value = "dataFinal", required = false) LocalDate dataFinal,
            Model model) {

        log.info("Iniciant procés per desactivar el vehicle amb matrícula: {}", matricula);

        Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);
        if (vehicle == null) {
            log.warn("No hi ha cap vehicle amb matrícula: {}", matricula);
            model.addAttribute("error", "No hi ha cap vehicle amb matrícula: " + matricula);
            return "redirect:/vehicle/llistar";
        }

        List<Reserva> reserves = reservaService.obtenirReservesPerMatricula(matricula);

        if (dataFinal != null) {
            log.info("Filtrant reserves fins a la data: {}", dataFinal);
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

        log.info("S'han carregat {} reserves per al vehicle amb matrícula: {}", reserves.size(), matricula);
        return "vehicle-desactivar";
    }

    /**
     * Desactiva les reserves seleccionades per un vehicle.
     *
     * @param idReservas Llista d'identificadors de reserves a desactivar.
     * @param matricula  Matrícula del vehicle associat.
     * @return Redirecció a la vista de llistat de vehicles.
     */
    @PostMapping("/desactivarReserves")
    public String desactivarReserves(@RequestParam(value = "idReservas", required = false) List<Long> idReservas,
            @RequestParam("matricula") String matricula) {

        log.info("Iniciant desactivació de reserves per al vehicle amb matrícula: {}", matricula);

        if (idReservas != null && !idReservas.isEmpty()) {
            for (Long id : idReservas) {
                reservaService.desactivarReserva(id);
                log.info("Reserva amb ID {} desactivada correctament.", id);

            }
        }
        vehicleService.desactivarVehicle(matricula);
        log.info("Vehicle amb matrícula {} desactivat correctament.", matricula);

        return "redirect:/vehicle/llistar";
    }

    /**
     * Mostra la vista per activar un vehicle desactivat.
     *
     * @param matricula Matrícula del vehicle a activar.
     * @param model     Model per passar dades a la vista.
     * @return Vista d'activació del vehicle.
     */
    @GetMapping("/activar/{matricula}")
    public String activarVehicles(@PathVariable("matricula") String matricula, Model model) {
        log.info("Carregant vista per activar el vehicle amb matrícula: {}", matricula);

        Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);
        model.addAttribute("vehicle", vehicle);
        return "vehicle-activar";
    }

    /**
     * Activa un vehicle i actualitza les dades opcionals com motiu i import.
     *
     * @param matricula Matrícula del vehicle a activar.
     * @param motiu     Motiu de l'activació (opcional).
     * @param importe   Import associat a l'activació (opcional).
     * @return Redirecció a la vista de llistat de vehicles.
     */
    @PostMapping("/activar/{matricula}")
    public String confirmarActivarVehicles(@PathVariable("matricula") String matricula,
            @RequestParam(value = "motiu", required = false) String motiu,
            @RequestParam(value = "importe", required = false) Double importe) {

        log.info("Iniciant activació del vehicle amb matrícula: {}", matricula);

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
        log.info("Vehicle amb matrícula {} activat correctament.", matricula);
        return "redirect:/vehicle/llistar";
    }
}
