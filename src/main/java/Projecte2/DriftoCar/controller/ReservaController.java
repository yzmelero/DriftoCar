package Projecte2.DriftoCar.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.entity.MySQL.Reserva;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MySQL.ClientRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;
import Projecte2.DriftoCar.service.MySQL.ReservaService;

/**
 * @author mario
 */
@Controller
@RequestMapping("/reserva")
public class ReservaController {
    @Autowired
    private ReservaService reservaService;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ClientRepository clientRepository;

    @GetMapping("/llistar")
    public String llistarReservas(Model model,
            @RequestParam(value = "searchEmail", required = false) String searchEmail,
            @RequestParam(value = "searchId_reserva", required = false) Long searchId_reserva) {

        if (searchEmail != null && searchEmail.isEmpty()) {
            searchEmail = null;
        }
        System.out.println("searchEmail: " + searchEmail);
        System.out.println("searchId_reserva: " + searchId_reserva);

        List<Reserva> reserves;
        if ((searchId_reserva != null)
                || (searchEmail != null && !searchEmail.isEmpty())) {
            reserves = reservaService.cercarReserva(searchEmail, searchId_reserva);
        } else {
            reserves = reservaService.llistarReservas();
        }

        model.addAttribute("reservas", reserves);
        model.addAttribute("searchId_reserva", searchId_reserva);
        model.addAttribute("searchEmail", searchEmail);

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

        reservaService.altaReserva(reserva);
        return "redirect:/reserva/llistar";
    }

}
