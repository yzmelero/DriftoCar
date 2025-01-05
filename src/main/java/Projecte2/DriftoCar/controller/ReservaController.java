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

import Projecte2.DriftoCar.entity.MongoDB.HistoricReserves;
import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.entity.MySQL.Reserva;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MongoDB.HistoricReservesRepository;
import Projecte2.DriftoCar.repository.MySQL.ClientRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import Projecte2.DriftoCar.service.MySQL.ReservaService;

/**
 * Controlador per gestionar les reserves del sistema.
 * Inclou funcionalitats per llistar, crear, modificar, consultar i cancel·lar
 * reserves.
 * També permet gestionar el lliurament i retorn de vehicles associats a les
 * reserves.
 * 
 * Aquest controlador està associat a la ruta base "/reserva".
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
    private HistoricReservesRepository historicReservesRepository;

    /**
     * Llista les reserves segons els filtres proporcionats i el rol de l'usuari
     * autenticat.
     *
     * @param model            Model per passar dades a la vista.
     * @param searchEmail      Filtre per correu electrònic del client.
     * @param searchId_reserva Filtre per ID de reserva.
     * @param searchMatricula  Filtre per matrícula del vehicle.
     * @param authentication   Informació de l'usuari autenticat.
     * @return Nom de la vista per mostrar les reserves.
     */
    @GetMapping("/llistar")
    public String llistarReservas(Model model,
            @RequestParam(value = "searchEmail", required = false) String searchEmail,
            @RequestParam(value = "searchId_reserva", required = false) Long searchId_reserva,
            @RequestParam(value = "searchMatricula", required = false) String searchMatricula,
            Authentication authentication) {

        log.info("Iniciant la llista de reserves amb filtres.");
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

        log.info("Usuari autenticat: {}, Rol: {}", username, rol);

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

        log.info("Nombre de reserves trobades: {}", reserves.size());
        model.addAttribute("reservas", reserves);
        model.addAttribute("searchId_reserva", searchId_reserva);
        model.addAttribute("searchEmail", searchEmail);
        model.addAttribute("searchMatricula", searchMatricula);

        return "reserva-llistar";
    }

    /**
     * Mostra l'històric de reserves, amb opció de filtrar per DNI.
     *
     * @param model el model utilitzat per passar dades a la vista.
     * @param dni   el DNI per filtrar les reserves (opcional).
     * @return la vista de l'històric de reserves.
     */
    @GetMapping("/historic")
    public String llistarHistoricReservas(Model model, @RequestParam(value = "dni", required = false) String dni) {
        log.info("Llistant l'històric de reserves amb filtre DNI: {}", dni);

        List<HistoricReserves> historicReserves;
        if (dni != null && !dni.isEmpty()) {
            historicReserves = historicReservesRepository.findByDNIContaining(dni);
            log.info("S'han trobat {} reserves amb el filtre DNI: {}", historicReserves.size(), dni);
        } else {
            historicReserves = historicReservesRepository.findAll();
            log.info("S'han trobat {} reserves sense cap filtre.", historicReserves.size());
        }
        model.addAttribute("reservas", historicReserves);
        model.addAttribute("dni", dni);
        return "reserva-historic";
    }

    /**
     * Mostra el formulari per donar d'alta una nova reserva.
     *
     * @param model          Model per passar dades a la vista.
     * @param authentication Informació de l'usuari autenticat.
     * @return Nom de la vista per mostrar el formulari d'alta.
     */
    @GetMapping("/alta")
    public String mostrarFormulari(Model model, Authentication authentication,
            @RequestParam(value = "vehicle", required = false) String matriculaVehicle) {

        log.info("Mostrant el formulari per crear una nova reserva.");

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

    /**
     * Processa l'alta d'una nova reserva.
     *
     * @param model   Model per passar dades a la vista.
     * @param reserva Reserva a donar d'alta.
     * @return Redirecció a la pàgina de llistat de reserves.
     */
    @PostMapping("/alta")
    public String altaReserva(Model model, @ModelAttribute("reserva") Reserva reserva) {
        log.info("Donant d'alta una nova reserva.");
        // Verificar que el client existeix
        reserva.setEstat(true);
        reservaService.altaReserva(reserva);
        log.info("Reserva creada amb èxit: {}", reserva);

        return "redirect:/reserva/llistar";
    }

    /**
     * Consulta els detalls d'una reserva específica.
     * 
     * @param idReserva ID de la reserva a consultar.
     * @param model     Model per passar dades a la vista.
     * @return Vista amb els detalls de la reserva o redirecció si no es troba.
     */
    @GetMapping("/consulta/{idReserva}")
    public String consultarReserva(@PathVariable Long idReserva, Model model) {

        log.info("Consultant la reserva amb ID: {}", idReserva);

        Optional<Reserva> reservaOptional = reservaService.cercaPerId(idReserva);
        Reserva reserva = reservaOptional.get();

        if (reserva == null) {
            log.warn("No s'ha trobat la reserva amb ID: {}", idReserva);
            model.addAttribute("error", "No s'ha trobat la reserva amb aquest id: " + idReserva);
            return "redirect:/reserva/llistar";
        }

        model.addAttribute("reserva", reserva);
        log.info("Reserva trobada: {}", reserva);
        return "reserva-consulta";
    }

    /**
     * Mostra el formulari per gestionar el lliurament d'un vehicle associat a una
     * reserva.
     *
     * @param model     Model per passar dades a la vista.
     * @param idReserva ID de la reserva a gestionar.
     * @return Nom de la vista del formulari de lliurament.
     */
    @GetMapping("/lliurar/{idReserva}")
    public String mostrarFormulariLliurament(Model model, @PathVariable Long idReserva) {

        log.info("Accedint al formulari de lliurament per la reserva amb ID: {}", idReserva);

        Optional<Reserva> reservaOptional = reservaService.cercaPerId(idReserva);
        Reserva reserva = reservaOptional.get();

        if (reserva == null) {
            log.warn("No s'ha trobat cap reserva amb ID: {}", idReserva);
            model.addAttribute("error", "No s'ha trobat cap reserva amb l'ID especificat.");
            return "error";
        }
        if (!reserva.isEstat()) {
            model.addAttribute("error", "No es pot lliurar un vehicle per a una reserva anul·lada.");
            return "reserva-llistar";
        }

        if (reserva.getDataLliurar() == null) {
            reserva.setDataLliurar(LocalDate.now());
            log.info("Assignada data de lliurament automàtica: {}", reserva.getDataLliurar());
        }

        model.addAttribute("reserva", reserva);
        log.info("Formulari preparat per al lliurament de la reserva: {}", reserva);

        return "reserva-lliurar";

    }

    /**
     * Processa el lliurament d'un vehicle associat a una reserva.
     *
     * @param model           Model per passar dades a la vista.
     * @param idReserva       ID de la reserva.
     * @param horaLliurar     Hora del lliurament en format HH:mm.
     * @param dataLliurar     Data del lliurament en format yyyy-MM-dd.
     * @param descripcioEstat Descripció de l'estat del vehicle lliurat.
     * @return Redirecció a la vista de consulta de la reserva.
     */
    @PostMapping("/lliurar/{idReserva}")
    public String lliurarVehicle(Model model, @PathVariable Long idReserva,
            @RequestParam("horaLliurar") String horaLliurar,
            @RequestParam("dataLliurar") String dataLliurar,
            @RequestParam("descripcioEstat") String descripcioEstat) {

        log.info("Processant lliurament per a la reserva amb ID: {}", idReserva);

        Optional<Reserva> reservaOptional = reservaService.cercaPerId(idReserva);
        Reserva reserva = reservaOptional.get();
        if (reserva == null) {
            log.warn("No s'ha trobat cap reserva amb ID: {}", idReserva);
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
            log.info("Lliurament completat per la reserva amb ID: {}", idReserva);
        } catch (DateTimeParseException e) {
            log.error("Error en el format de la data/hora de lliurament.");
            model.addAttribute("error", "El format de la data/hora és incorrecte.");
            return "reserva-lliurar"; // Torna a mostrar el formulari amb un missatge d'error
        }

        return "redirect:/reserva/consulta/{idReserva}";
    }

    /**
     * Mostra el formulari per gestionar el retorn d'un vehicle associat a una
     * reserva.
     *
     * @param model     Model per passar dades a la vista.
     * @param idReserva ID de la reserva a gestionar.
     * @return Nom de la vista del formulari de retorn.
     */
    @GetMapping("/retornar/{idReserva}")
    public String mostrarFormulariRetornar(Model model, @PathVariable Long idReserva) {

        log.info("Accedint al formulari de retorn per la reserva amb ID: {}", idReserva);

        Optional<Reserva> reservaOptional = reservaService.cercaPerId(idReserva);
        Reserva reserva = reservaOptional.get();
        if (reserva == null) {
            log.warn("No s'ha trobat cap reserva amb ID: {}", idReserva);
            model.addAttribute("error", "No s'ha trobat cap reserva amb l'ID especificat.");
            return "error";
        }

        model.addAttribute("reserva", reserva);
        log.info("Formulari preparat per al retorn de la reserva: {}", reserva);

        return "reserva-retornar";

    }

    /**
     * Processa el retorn d'un vehicle associat a una reserva.
     *
     * @param model      Model per passar dades a la vista.
     * @param idReserva  ID de la reserva.
     * @param dataRetorn Data del retorn en format yyyy-MM-dd.
     * @param horaRetorn Hora del retorn en format HH:mm.
     * @param action     Acció a realitzar després del retorn (p.e. obrir
     *                   incidència).
     * @return Redirecció a la vista de consulta de la reserva o a l'incidència.
     */
    @PostMapping("/retornar/{idReserva}")
    public String retornarVehicle(Model model, @PathVariable Long idReserva,
            @RequestParam("dataRetorn") LocalDate dataRetorn,
            @RequestParam("horaRetorn") LocalTime horaRetorn,
            @RequestParam String action) {

        log.info("Processant retorn per a la reserva amb ID: {}", idReserva);

        Optional<Reserva> reservaOptional = reservaService.cercaPerId(idReserva);
        Reserva reserva = reservaOptional.get();

        if (reserva == null) {
            log.warn("No s'ha trobat cap reserva amb ID: {}", idReserva);
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
            log.info("Retorn completat per la reserva amb ID: {}", idReserva);

        } catch (DateTimeParseException e) {
            log.error("Error en el format de la data/hora de retorn.");
            model.addAttribute("error", "El format de la data/hora és incorrecte.");
            return "reserva-retornar"; // Torna a mostrar el formulari amb un missatge d'error
        }

        if ("incidencia".equals(action)) {
            log.info("Redirigint a l'obertura d'incidència per al vehicle amb matrícula: {}",
                    reserva.getVehicle().getMatricula());
            return "redirect:/incidencia/obrir/" + reserva.getVehicle().getMatricula();
        }

        return "redirect:/reserva/consulta/{idReserva}";
    }

    /**
     * Calcula el preu total i la fiança d'una reserva en procés de retorn.
     *
     * @param model      Model per passar dades a la vista.
     * @param idReserva  ID de la reserva.
     * @param dataRetorn Data del retorn en format yyyy-MM-dd.
     * @param horaRetorn Hora del retorn en format HH:mm.
     * @return Vista del formulari de retorn actualitzada amb el càlcul del preu.
     */
    @PostMapping("/retornar/calculPreu/{idReserva}")
    public String calcularPreuRetorn(Model model, @PathVariable Long idReserva,
            @RequestParam("dataRetorn") String dataRetorn,
            @RequestParam("horaRetorn") String horaRetorn) {

        log.info("Iniciant el càlcul de preu per al retorn de la reserva amb ID: {}", idReserva);

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
            log.info("Càlcul complet per a la reserva amb ID: {}. Fiança: {}, Cost Total: {}", idReserva, fianca,
                    costTotal);

        } catch (DateTimeParseException e) {
            log.error("Error en el format de la data/hora de retorn.");
            model.addAttribute("error", "Els formats data/hora són incorrectes");
            return "reserva-retornar";
        }
        return "reserva-retornar";
    }

    /**
     * Calcula el preu total i la fiança d'una reserva en procés d'alta.
     *
     * @param model     Model per passar dades a la vista.
     * @param dataInici Data d'inici de la reserva en format yyyy-MM-dd.
     * @param horaInici Hora d'inici de la reserva en format HH:mm.
     * @param dataFi    Data de fi de la reserva en format yyyy-MM-dd.
     * @param horaFi    Hora de fi de la reserva en format HH:mm.
     * @param costTotal Cost total calculat (opcional).
     * @param fianca    Fiança calculada (opcional).
     * @param reserva   Reserva amb dades inicials.
     * @return Vista del formulari d'alta actualitzada amb el càlcul del preu.
     */
    @PostMapping("/alta/calculPreu")
    public String calcularPreu(Model model,
            @RequestParam("dataInici") String dataInici,
            @RequestParam("horaInici") String horaInici,
            @RequestParam("dataFi") String dataFi,
            @RequestParam("horaFi") String horaFi,
            @RequestParam(required = false, defaultValue = "0") Double costTotal,
            @RequestParam(required = false, defaultValue = "0") Double fianca,
            Reserva reserva) {

        log.info("Iniciant càlcul de preu per a una nova reserva.");

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
                    .orElseThrow(() -> {
                        log.error("Client no trobat amb DNI: {}", reserva.getClient().getDni());
                        return new IllegalArgumentException("Client no trobat");
                    }));
            reserva.setVehicle(vehicleRepository.findByMatricula(reserva.getVehicle().getMatricula())
                    .orElseThrow(() -> {
                        log.error("Vehicle no trobat amb matrícula: {}", reserva.getVehicle().getMatricula());
                        return new IllegalArgumentException("Vehicle no trobat");
                    }));

            log.info("Assignats vehicle {} i client {} a la reserva.", reserva.getVehicle(), reserva.getClient());

            fianca = reservaService.calculFianca(reserva);
            costTotal = reservaService.calculPreuReserva(reserva);

            model.addAttribute("clients", reserva.getClient()); // Manté la llista de clients
            model.addAttribute("vehicles", reserva.getVehicle()); // Manté la llista de vehicles
            model.addAttribute("reserva", reserva);
            model.addAttribute("fianca", fianca);
            model.addAttribute("costTotal", costTotal);
            log.info("Càlcul complet: Fiança: {}, Cost Total: {}", fianca, costTotal);
        } catch (DateTimeParseException e) {
            log.error("Error en el format de la data/hora.");
            model.addAttribute("error", "Els formats data/hora són incorrectes");
            return "reserva-alta";
        }
        return "reserva-alta";
    }

    /**
     * Anula una reserva específica pel seu ID.
     * 
     * @param id    ID de la reserva a anular.
     * @param model Model per passar dades a la vista.
     * @return Redirecció a la vista de llistat de reserves.
     */
    @PostMapping("/anular/{id}")
    public String anularReserva(@PathVariable Long id, Model model) {
        log.info("Anul·lant la reserva amb ID: {}", id);

        try {
            reservaService.desactivarReserva(id);
            log.info("Reserva amb ID: {} anul·lada correctament.", id);

        } catch (Exception e) {
            log.error("Error durant l'anul·lació de la reserva amb ID: {}: {}", id, e.getMessage());
            model.addAttribute("error", e.getMessage());
        }
        return "redirect:/reserva/llistar";
    }

    /**
     * Obté les dates no disponibles per a un vehicle segons la seva matrícula.
     *
     * @param matricula la matrícula del vehicle.
     * @return una resposta HTTP amb la llista de dates no disponibles.
     */
    @GetMapping("/no-disponibles/{matricula}")
    public ResponseEntity<List<String>> obtenerFechasNoDisponibles(@PathVariable String matricula) {
        log.info("Obtenint les dates no disponibles per a la matrícula: {}", matricula);
        List<String> fechasNoDisponibles = reservaService.obtenerFechasNoDisponibles(matricula);
        log.info("Dates no disponibles per a la matrícula {}: {}", matricula, fechasNoDisponibles);
        return ResponseEntity.ok(fechasNoDisponibles);
    }
}
