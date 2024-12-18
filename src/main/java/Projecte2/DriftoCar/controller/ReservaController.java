package Projecte2.DriftoCar.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

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

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.entity.MySQL.Reserva;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MySQL.ClientRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import Projecte2.DriftoCar.service.MySQL.ReservaService;

/**
 * @author mario
 */
@Controller
@RequestMapping("/reserva")
@Scope("session")
public class ReservaController {

    Logger log = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    private ReservaService reservaService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/llistar")
    public String llistarReservas(Model model,
            @RequestParam(value = "searchEmail", required = false) String searchEmail,
            @RequestParam(value = "searchId_reserva", required = false) Long searchId_reserva,
            @RequestParam(value = "searchMatricula", required = false) String searchMatricula) {

        if (searchEmail != null && searchEmail.isEmpty()) {
            searchEmail = null;
        }
        if (searchMatricula != null && searchMatricula.isEmpty()) {
            searchMatricula = null;
        }

        // Verificar per consola que funcioni correctament.
        log.debug("searchEmail: " + searchEmail);
        log.debug("searchId_reserva: " + searchId_reserva);
        log.debug("searchMatricula: " + searchMatricula);

        List<Reserva> reserves;
        if ((searchId_reserva != null)
                || (searchEmail != null && !searchEmail.isEmpty())
                || (searchMatricula != null && !searchMatricula.isEmpty())) {
            reserves = reservaService.cercarReserva(searchEmail, searchId_reserva, searchMatricula);
        } else {
            reserves = reservaService.llistarReservas();
        }

        model.addAttribute("reservas", reserves);
        model.addAttribute("searchId_reserva", searchId_reserva);
        model.addAttribute("searchEmail", searchEmail);
        model.addAttribute("searchMatricula", searchMatricula);

        return "reserva-llistar";
    }

    @GetMapping("/alta")
    public String mostrarFormulari(Model model) {

        // Aqui creem una reserva buida per a poder mostrar el formulari.
        List<Client> clients = clientRepository.findAll();
        List<Vehicle> vehicles = vehicleRepository.findAll();

        // model.addAttribute("reserva", new Reserva());

        Reserva reserva = new Reserva();

        model.addAttribute("clients", clients);
        model.addAttribute("vehicles", vehicles);
        model.addAttribute("reserva", reserva);
        return "reserva-alta";
    }

    @PostMapping("/alta")
    public String altaReserva(Model model, @ModelAttribute("reserva") Reserva reserva) {
        Optional<Vehicle> vehicle = vehicleRepository.findByMatricula(reserva.getVehicle().getMatricula());

        // Verificar que el client existeix
        Optional<Client> client = clientRepository.findByDni(reserva.getClient().getDni());
        reserva.setEstat(true);
        reservaService.altaReserva(reserva);
        return "redirect:/reserva/llistar";
    }

    @GetMapping("/consulta/{idReserva}")
    public String consultarReserva(@PathVariable Long idReserva, Model model) {
        Reserva reserva = reservaService.cercaPerId(idReserva);

        if (reserva == null) {
            model.addAttribute("error", "No s'ha trobat la reserva amb aquest id: " + idReserva);
            return "redirect:/reserva/llistar";
        }

        model.addAttribute("reserva", reserva);
        return "reserva-consulta";
    }

    @GetMapping("/lliurar/{idReserva}")
    public String mostrarFormulariLliurament(Model model, @PathVariable Long idReserva) {

        Reserva reserva = reservaService.cercaPerId(idReserva);
        if (reserva == null) {
            model.addAttribute("error", "No s'ha trobat cap reserva amb l'ID especificat.");
            return "error";
        }

        model.addAttribute("reserva", reserva);
        return "reserva-lliurar";

    }

    @PostMapping("/lliurar/{idReserva}")
    public String lliurarVehicle(Model model, @PathVariable Long idReserva,
            @RequestParam("dataLliurar") String dataLliurar,
            @RequestParam("horaLliurar") String horaLliurar,
            @RequestParam("descripcioEstat") String descripcioEstat) {

        Reserva reserva = reservaService.cercaPerId(idReserva);
        if (reserva == null) {
            model.addAttribute("error", "No s'ha trobat cap reserva amb l'ID especificat.");
            return "error";
        }

        try {

            DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalDate data = LocalDate.parse(dataLliurar, dataFormatter);
            LocalTime hora = LocalTime.parse(horaLliurar, horaFormatter);

            reserva.setDataLliurar(data);
            reserva.setHoraLliurar(hora);
            reserva.setDescripcioEstatLliurar(descripcioEstat);
            reserva.setEstat(true);

            reservaService.modificarReserva(reserva);
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "El format de la data/hora és incorrecte.");
            return "reserva-lliurar"; // Torna a mostrar el formulari amb un missatge d'error
        }

        return "redirect:/reserva/consulta/{idReserva}";
    }

    @PostMapping("/anular/{id}")
    public String anularReserva(@PathVariable Long id, Model model) {
        try {
            reservaService.desactivarReserva(id);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/reserva/llistar";
    }
}
