package Projecte2.DriftoCar.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import Projecte2.DriftoCar.entity.MongoDB.HistoricReserves;
import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.entity.MySQL.Reserva;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MongoDB.HistoricReservesRepository;
import Projecte2.DriftoCar.repository.MySQL.ClientRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import Projecte2.DriftoCar.service.MySQL.ReservaService;
import Projecte2.DriftoCar.service.MySQL.VehicleService;

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

    @Autowired
    private ClientService clientService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private HistoricReservesRepository historicReservesRepository;

    @GetMapping("/llistar")
    public String llistarReservas(Model model,
            @RequestParam(value = "searchEmail", required = false) String searchEmail,
            @RequestParam(value = "searchId_reserva", required = false) Long searchId_reserva,
            @RequestParam(value = "searchMatricula", required = false) String searchMatricula,
            Authentication authentication) {

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

        String username = authentication.getName();
        String rol = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("UNKNOWN");

        List<Reserva> reserves;
        if (rol.equals("ROLE_ADMIN")) {
            // Admin ve todas las reservas con filtros
            if (searchId_reserva != null || searchEmail != null || searchMatricula != null) {
                reserves = reservaService.cercarReserva(searchEmail, searchId_reserva, searchMatricula);
            } else {
                reserves = reservaService.llistarReservas();
            }
        } else if (rol.equals("ROLE_CLIENT")) {
            // Cliente solo ve sus reservas
            reserves = reservaService.cercarReservesPerClient(username, searchEmail, searchId_reserva, searchMatricula);
        } else if (rol.equals("ROLE_AGENT")) {
            // Agente ve sus reservas y las de vehículos a su cargo
            reserves = reservaService.cercarReservesPerAgent(username, searchEmail, searchId_reserva, searchMatricula);
        } else {
            reserves = new ArrayList<>();
        }

        reserves.sort((r1, r2) -> Long.compare(r2.getIdReserva(), r1.getIdReserva()));

        model.addAttribute("reservas", reserves);
        model.addAttribute("searchId_reserva", searchId_reserva);
        model.addAttribute("searchEmail", searchEmail);
        model.addAttribute("searchMatricula", searchMatricula);

        return "reserva-llistar";
    }

    @GetMapping("/historic")
    public String llistarHistoricReservas(Model model, @RequestParam(value = "dni", required = false) String dni) {
        List<HistoricReserves> historicReserves;
        if (dni != null && !dni.isEmpty()) {
            historicReserves = historicReservesRepository.findByDNIContaining(dni);
        } else {
            historicReserves = historicReservesRepository.findAll();
        }
        model.addAttribute("reservas", historicReserves);
        model.addAttribute("dni", dni);
        return "reserva-historic";
    }

    @GetMapping("/alta")
    public String mostrarFormulari(Model model, Authentication authentication,
            @RequestParam(value = "vehicle", required = false) String matriculaVehicle) {

        String username = authentication.getName();
        String rol = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .orElse("UNKNOWN");

        if (rol.equals("ROLE_ADMIN")) {
            // Admin ve todas las reservas con filtros
            List<Client> clients = clientRepository.findAll();
            model.addAttribute("clients", clients);
        } else if (rol.equals("ROLE_CLIENT") || rol.equals("ROLE_AGENT")) {
            // Cliente solo ve sus reservas
            Optional<Client> client = clientService.findByUsuari(username);
            Client existent = client.get();
            model.addAttribute("clients", existent);
        }

        if (matriculaVehicle != null) {
            Vehicle vehicle = vehicleRepository.findByMatricula(matriculaVehicle).get();
            model.addAttribute("vehicles", vehicle);

        } else {
            List<Vehicle> vehicles = vehicleRepository.findByDisponibilitat(true);
            model.addAttribute("vehicles", vehicles);

        }

        Reserva reserva = new Reserva();

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
        Optional<Reserva> reservaOptional = reservaService.cercaPerId(idReserva);
        Reserva reserva = reservaOptional.get();

        if (reserva == null) {
            model.addAttribute("error", "No s'ha trobat la reserva amb aquest id: " + idReserva);
            return "redirect:/reserva/llistar";
        }

        model.addAttribute("reserva", reserva);
        return "reserva-consulta";
    }

    @GetMapping("/lliurar/{idReserva}")
    public String mostrarFormulariLliurament(Model model, @PathVariable Long idReserva) {

        Optional<Reserva> reservaOptional = reservaService.cercaPerId(idReserva);
        Reserva reserva = reservaOptional.get();

        if (reserva == null) {
            model.addAttribute("error", "No s'ha trobat cap reserva amb l'ID especificat.");
            return "error";
        }
        if (!reserva.isEstat()) {
            model.addAttribute("error", "No es pot lliurar un vehicle per a una reserva anul·lada.");
            return "reserva-llistar";
        }

        if (reserva.getDataLliurar() == null) {
            reserva.setDataLliurar(LocalDate.now());
        }

        model.addAttribute("reserva", reserva);
        return "reserva-lliurar";

    }

    @PostMapping("/lliurar/{idReserva}")
    public String lliurarVehicle(Model model, @PathVariable Long idReserva,
            @RequestParam("horaLliurar") String horaLliurar,
            @RequestParam("dataLliurar") String dataLliurar,
            @RequestParam("descripcioEstat") String descripcioEstat) {

        Optional<Reserva> reservaOptional = reservaService.cercaPerId(idReserva);
        Reserva reserva = reservaOptional.get();
        if (reserva == null) {
            model.addAttribute("error", "No s'ha trobat cap reserva amb l'ID especificat.");
            return "error";
        }

        try {

            DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");
            LocalDate data = LocalDate.parse(dataLliurar, dataFormatter); // Assignem la data actual
            // LocalDate data = LocalDate.parse(dataLliurar, dataFormatter);
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

    @GetMapping("/retornar/{idReserva}")
    public String mostrarFormulariRetornar(Model model, @PathVariable Long idReserva) {

        Optional<Reserva> reservaOptional = reservaService.cercaPerId(idReserva);
        Reserva reserva = reservaOptional.get();
        if (reserva == null) {
            model.addAttribute("error", "No s'ha trobat cap reserva amb l'ID especificat.");
            return "error";
        }

        model.addAttribute("reserva", reserva);

        return "reserva-retornar";

    }

    @PostMapping("/retornar/{idReserva}")
    public String retornarVehicle(Model model, @PathVariable Long idReserva,
            @RequestParam("dataRetorn") LocalDate dataRetorn,
            @RequestParam("horaRetorn") LocalTime horaRetorn,
            @RequestParam String action) {

        Optional<Reserva> reservaOptional = reservaService.cercaPerId(idReserva);
        Reserva reserva = reservaOptional.get();

        if (reserva == null) {
            model.addAttribute("error", "No s'ha trobat cap reserva amb l'ID especificat.");
            return "error";
        }

        try {

            reserva.setDataRetornar(dataRetorn);
            reserva.setHoraRetornar(horaRetorn);
            reserva.setEstat(false);

            reserva.setCostTotal(reservaService.calculPreuTotal(reserva));
            reserva.setFianca(reservaService.calculFianca(reserva));

            reservaService.modificarReserva(reserva);
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "El format de la data/hora és incorrecte.");
            return "reserva-retornar"; // Torna a mostrar el formulari amb un missatge d'error
        }

        if ("incidencia".equals(action)) {
            return "redirect:/incidencia/obrir/" + reserva.getVehicle().getMatricula();
        }

        return "redirect:/reserva/consulta/{idReserva}";
    }

    @PostMapping("/retornar/calculPreu/{idReserva}")
    public String calcularPreuRetorn(Model model, @PathVariable Long idReserva,
            @RequestParam("dataRetorn") String dataRetorn,
            @RequestParam("horaRetorn") String horaRetorn) {

        Optional<Reserva> reservaOptional = reservaService.cercaPerId(idReserva);
        Reserva reserva = reservaOptional.get();

        try {
            DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalDate data = LocalDate.parse(dataRetorn, dataFormatter);
            LocalTime hora = LocalTime.parse(horaRetorn, horaFormatter);

            reserva.setDataRetornar(data);
            reserva.setHoraRetornar(hora);

            double fianca = reservaService.calculFianca(reserva);
            double costTotal = reservaService.calculPreuTotal(reserva);

            model.addAttribute("reserva", reserva);
            model.addAttribute("fianca", fianca);
            model.addAttribute("costTotal", costTotal);
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "Els formats data/hora són incorrectes");
            return "reserva-retornar";
        }
        return "reserva-retornar";
    }

    @PostMapping("/alta/calculPreu")
    public String calcularPreu(Model model,
            @RequestParam("dataInici") String dataInici,
            @RequestParam("horaInici") String horaInici,
            @RequestParam("dataFi") String dataFi,
            @RequestParam("horaFi") String horaFi,
            @RequestParam(required = false, defaultValue = "0") Double costTotal,
            @RequestParam(required = false, defaultValue = "0") Double fianca,
            Reserva reserva) {

        try {
            DateTimeFormatter dataFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            DateTimeFormatter horaFormatter = DateTimeFormatter.ofPattern("HH:mm");

            LocalDate dataIniciFin = LocalDate.parse(dataInici, dataFormatter);
            LocalDate dataFiFin = LocalDate.parse(dataFi, dataFormatter);

            LocalTime horaIniciFin = LocalTime.parse(horaInici, horaFormatter);
            LocalTime horaFiFin = LocalTime.parse(horaFi, horaFormatter);

            reserva.setDataInici(dataIniciFin);
            reserva.setDataFi(dataFiFin);
            reserva.setHoraInici(horaIniciFin);
            reserva.setHoraFi(horaFiFin);

            reserva.setClient(clientRepository.findByDni(reserva.getClient().getDni())
                    .orElseThrow(() -> new IllegalArgumentException("Client no trobat")));
            reserva.setVehicle(vehicleRepository.findByMatricula(reserva.getVehicle().getMatricula())
                    .orElseThrow(() -> new IllegalArgumentException("Vehicle no trobat")));

            System.out.println("Vehicle: " + reserva.getVehicle());
            System.out.println("Client: " + reserva.getClient());
            System.out.println("Data Inici: " + reserva.getDataInici());
            System.out.println("Data Fi: " + reserva.getDataFi());

            fianca = reservaService.calculFianca(reserva);
            costTotal = reservaService.calculPreuReserva(reserva);

            model.addAttribute("clients", reserva.getClient()); // Manté la llista de clients
            model.addAttribute("vehicles", reserva.getVehicle()); // Manté la llista de vehicles
            model.addAttribute("reserva", reserva);
            model.addAttribute("fianca", fianca);
            model.addAttribute("costTotal", costTotal);
        } catch (DateTimeParseException e) {
            model.addAttribute("error", "Els formats data/hora són incorrectes");
            return "reserva-alta";
        }
        return "reserva-alta";
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

    @GetMapping("/no-disponibles/{matricula}")
    public ResponseEntity<List<String>> obtenerFechasNoDisponibles(@PathVariable String matricula) {
        List<String> fechasNoDisponibles = reservaService.obtenerFechasNoDisponibles(matricula);
        return ResponseEntity.ok(fechasNoDisponibles);
    }
}
