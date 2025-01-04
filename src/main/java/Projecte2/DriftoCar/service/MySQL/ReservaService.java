/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Projecte2.DriftoCar.entity.MongoDB.HistoricReserves;
import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.entity.MySQL.Reserva;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MongoDB.HistoricReservesRepository;
import Projecte2.DriftoCar.repository.MySQL.ClientRepository;
import Projecte2.DriftoCar.repository.MySQL.ReservaRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;

import java.time.temporal.ChronoUnit;

/**
 * Classe de servei per a la gestió de reserves de vehicles.
 * Proporciona mètodes per a crear, modificar, desactivar i cercar reserves,
 * així com calcular el preu total de les mateixes.
 */
@Service
public class ReservaService {

    Logger log = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private HistoricReservesRepository historicReservesRepository;

    /**
     * Aquest metode guarda una nova reserva a la base de dades si el client i la
     * matrícula existeixen.
     *
     * @param reserva Reserva a guardar
     * @return Reserva guardada
     * @throws Exception si el vehicle o client no existeixen
     */
    public Reserva altaReserva(Reserva reserva) {
        log.info("Iniciant el mètode altaReserva del servei amb reserva: {}", reserva);

        log.debug("Comprovant si el vehicle existeix amb matrícula: {}", reserva.getVehicle().getMatricula());
        // Verificar que el vehículo existe
        Optional<Vehicle> vehicle = vehicleRepository.findByMatricula(reserva.getVehicle().getMatricula());

        log.debug("Comprovant si el client existeix amb DNI: {}", reserva.getClient().getDni());
        // Verificar que el cliente existe
        Optional<Client> client = clientRepository.findByDni(reserva.getClient().getDni());

        if (client.isEmpty()) {
            log.error("El client amb DNI {} no existeix", reserva.getClient().getDni());
            throw new RuntimeException("El client no existeix");
        }
        if (vehicle.isEmpty()) {
            log.error("El vehicle amb matrícula {} no existeix", reserva.getVehicle().getMatricula());
            throw new RuntimeException("El vehicle no existeix");
        }

        Vehicle vehicleNou = vehicle.get();
        if (!vehicleNou.isDisponibilitat()) {
            log.warn("El vehicle amb matrícula {} no està disponible", vehicleNou.getMatricula());
            throw new RuntimeException("El vehicle no esta disponible");
        }

        log.info("Assignant client i vehicle a la reserva.");
        reserva.setClient(client.get());
        reserva.setVehicle(vehicle.get());

        log.info("Guardant la reserva a la base de dades.");
        Reserva savedReserva = reservaRepository.save(reserva);

        log.info("Creant el registre històric de la reserva.");
        // Crear el histórico después de guardar la reserva
        HistoricReserves historic = new HistoricReserves();
        historic.setIdReserva(savedReserva.getIdReserva().toString()); // Ahora el ID está disponible
        historic.setNomClient(savedReserva.getClient().getNom());
        historic.setCognomClient(savedReserva.getClient().getCognoms());
        historic.setDNI(savedReserva.getClient().getDni());
        historic.setMatricula(savedReserva.getVehicle().getMatricula());
        historic.setDataInici(savedReserva.getDataInici());
        historic.setDataFi(savedReserva.getDataFi());
        historic.setTotalCost(calculPreuReserva(savedReserva));
        historic.setFianca(calculFianca(savedReserva));
        historic.setEstat(savedReserva.isEstat());

        log.debug("Guardant el registre històric a la base de dades.");
        historicReservesRepository.save(historic);

        log.info("Reserva guardada correctament amb ID: {}", savedReserva.getIdReserva());
        return savedReserva;
    }

    /**
     * Obté totes les reserves registrades a la base de dades.
     *
     * @return Llista de reserves
     */
    public List<Reserva> llistarReservas() {
        log.info("Obtenint totes les reserves des de la base de dades.");
        return reservaRepository.findAll();
    }

    /**
     * Cerca una reserva per ID.
     *
     * @param idReserva ID de la reserva a cercar
     * @return Reserva trobada
     */
    public Optional<Reserva> cercaPerId(Long idReserva) {
        log.info("Buscant reserva amb ID: {}", idReserva);
        return reservaRepository.findById(idReserva);
    }

    /**
     * Cerca reserves segons els filtres proporcionats (email, ID de reserva,
     * matrícula).
     *
     * @param email      Email del client
     * @param id_reserva ID de la reserva
     * @param matricula  Matricula del vehicle
     * @return Llista de reserves trobades
     */
    public List<Reserva> cercarReserva(String email, Long id_reserva, String matricula) {
        log.debug("cercarReserva() - email: " + email + ", id_reserva: " + id_reserva + ", matricula: " + matricula);
        return reservaRepository.cercarReserves(id_reserva, email, matricula);

    }

    /**
     * Modifica una reserva existent.
     *
     * @param reserva Reserva amb les dades modificades
     */
    public void modificarReserva(Reserva reserva) {
        log.info("Guardant reserva amb ID: " + reserva.getIdReserva());
        reservaRepository.save(reserva);
        log.info("Reserva guardada correctament.");
    }

    /**
     * Obté les reserves d'un vehicle segons la seva matrícula.
     *
     * @param matricula Matrícula del vehicle
     * @return Llista de reserves
     */
    public List<Reserva> obtenirReservesPerMatricula(String matricula) {
        log.info("Buscant reserves per matrícula: {}", matricula);
        return reservaRepository.findByVehicleMatriculaEstat(matricula);
    }

    /**
     * Desactiva una reserva marcant-la com a no activa.
     *
     * @param idReserva ID de la reserva a desactivar
     * @return Reserva desactivada
     */
    public Reserva desactivarReserva(Long idReserva) {
        log.info("Intentant desactivar la reserva amb ID: {}", idReserva);
        Reserva reserva = reservaRepository.findById(idReserva)
                .orElseThrow(() -> new RuntimeException("Reserva " + idReserva + " no trobada: "));
        if (reserva.isEstat()) {
            log.info("La reserva està activa. Procedint a desactivar-la.");
            reserva.setEstat(false); // Marca la reserva como inactiva
            HistoricReserves historic = historicReservesRepository.findById(reserva.getIdReserva().toString())
                    .orElseThrow(() -> new RuntimeException(
                            "Reserva " + idReserva + " no trobada en el historic de reserves: "));
            historic.setEstat(false);
            historicReservesRepository.save(historic);
            log.info("Reserva desactivada correctament amb ID: {}", idReserva);
            return reservaRepository.save(reserva);
        } else {
            log.warn("La reserva amb ID {} ja està anul·lada.", idReserva);
            throw new RuntimeException("La reserva ja està anul·lada");
        }
    }

    /**
     * Calcula la fiança d'una reserva tenint en compte si el client és premium.
     *
     * @param reserva Reserva per a la qual calcular la fiança
     * @return Fiança calculada
     */
    public double calculFianca(Reserva reserva) {
        log.info("Calculant la fiança al servei per la reserva amb ID: {}", reserva.getIdReserva());
        Client client = reserva.getClient();

        boolean esClientPremium = client != null && client.isReputacio();
        double fiancaBase = reserva.getVehicle().getFianca();
        log.info("La fiança inicial és: " + fiancaBase);
        double fiancaAmbDescompte = esClientPremium ? fiancaBase * 0.75 : fiancaBase;
        log.info("La fiança calculada és: " + fiancaAmbDescompte);
        return fiancaAmbDescompte;
    }

    /**
     * Calcula el preu total final d'una reserva, tenint en compte el temps de
     * lloguer i qualsevol penalització per retard.
     *
     * @param reserva Reserva per la qual calcular el preu
     * @return Preu total de la reserva
     * @throws IllegalArgumentException si les dates i hores són nules
     */
    public double calculPreuTotal(Reserva reserva) {
        log.info("Calculant el preu total al servei per la reserva amb ID: {}", reserva.getIdReserva());

        if (reserva.getHoraInici() == null || reserva.getHoraLliurar() == null
                || reserva.getHoraFi() == null || reserva.getHoraRetornar() == null
                || reserva.getDataLliurar() == null || reserva.getDataRetornar() == null) {
            throw new IllegalArgumentException("Les dates i hores no poden ser null");
        }

        log.info("Data Lliurar: " + reserva.getDataLliurar());
        log.info("Hora Lliurar: " + reserva.getHoraLliurar());
        log.info("Data Retornar: " + reserva.getDataRetornar());
        log.info("Hora Retornar: " + reserva.getHoraRetornar());

        // Combinar dates i hores
        LocalDateTime iniciLliurament = LocalDateTime.of(reserva.getDataLliurar(), reserva.getHoraLliurar());
        LocalDateTime fiRetorn = LocalDateTime.of(reserva.getDataRetornar(), reserva.getHoraRetornar());

        // Diferència total en hores
        long horesTotals = ChronoUnit.HOURS.between(iniciLliurament, fiRetorn);

        // Penalització per retard
        LocalDateTime fiPrevist = LocalDateTime.of(reserva.getDataFi(), reserva.getHoraFi());
        long horesRetard = ChronoUnit.HOURS.between(fiPrevist, fiRetorn);
        horesRetard = Math.max(0, horesRetard); // Assegurar que no sigui negatiu

        log.info("Hores Totals: " + horesTotals);
        log.info("Hores Retard: " + horesRetard);

        // Cost per hora
        double costHora = reserva.getVehicle().getCostHora();
        log.info("Cost Hora Vehicle: " + costHora);

        double fianca = calculFianca(reserva);
        log.info("Fiança Calculada: " + fianca);

        // Cost total sense penalització
        double costTotalSensePenalitzacio = horesTotals * costHora;

        // Penalització pel retard
        double costPenalitzacio = horesRetard * costHora;

        // Càlcul del preu total
        double preuTotal = costTotalSensePenalitzacio + costPenalitzacio + fianca;

        return Math.max(preuTotal, 0);
    }

    /**
     * Calcula el preu estimat d'una reserva sense tenir en compte penalitzacions
     * per retard.
     *
     * @param reserva Reserva per la qual calcular el preu
     * @return Preu de la reserva
     */
    public double calculPreuReserva(Reserva reserva) {
        log.info("Calculant el preu estimat per la reserva amb ID: {}", reserva.getIdReserva());

        log.info("Data Inici: " + reserva.getDataInici());
        log.info("Hora Inici: " + reserva.getHoraInici());
        log.info("Data Fi: " + reserva.getDataFi());
        log.info("Hora Fi: " + reserva.getHoraFi());

        // Combinar dates i hores
        LocalDateTime inici = LocalDateTime.of(reserva.getDataInici(), reserva.getHoraInici());
        LocalDateTime fi = LocalDateTime.of(reserva.getDataFi(), reserva.getHoraFi());

        // Diferència total en hores
        long horesTotals = ChronoUnit.HOURS.between(inici, fi);

        // Cost per hora
        double costHora = reserva.getVehicle().getCostHora();
        log.info("Cost Hora Vehicle: " + costHora);

        double fianca = calculFianca(reserva);
        log.info("Fiança Calculada: " + fianca);

        double costTotalSensePenalitzacio = horesTotals * costHora;

        double preuTotal = costTotalSensePenalitzacio + fianca;

        return Math.max(preuTotal, 0);
    }

    /**
     * Cerca les reserves associades a un client, aplicant opcionalment filtres
     * addicionals.
     *
     * @param username         El nom d'usuari del client.
     * @param searchEmail      El correu electrònic del client (opcional).
     * @param searchId_reserva L'ID de la reserva a cercar (opcional).
     * @param searchMatricula  La matrícula del vehicle a cercar (opcional).
     * @return Una llista de reserves que coincideixen amb els criteris de cerca.
     */
    public List<Reserva> cercarReservesPerClient(String username, String searchEmail, Long searchId_reserva,
            String searchMatricula) {
        log.info("Cercant reserves per al client amb usuari: {}", username);
        return reservaRepository.findByClientUsuariAndFilters(username, searchEmail, searchId_reserva, searchMatricula);
    }

    /**
     * Cerca les reserves gestionades per un agent, aplicant opcionalment filtres
     * addicionals.
     *
     * @param username         El nom d'usuari de l'agent.
     * @param searchEmail      El correu electrònic associat a les reserves
     *                         (opcional).
     * @param searchId_reserva L'ID de la reserva a cercar (opcional).
     * @param searchMatricula  La matrícula del vehicle a cercar (opcional).
     * @return Una llista de reserves que coincideixen amb els criteris de cerca.
     */
    public List<Reserva> cercarReservesPerAgent(String username, String searchEmail, Long searchId_reserva,
            String searchMatricula) {
        log.info("Cercant reserves per l'agent amb usuari: {}", username);

        List<String> matriculesVehicles = vehicleRepository.findMatriculesByAgentUsuari(username);
        return reservaRepository.findByAgentAndFilters(username, matriculesVehicles, searchEmail, searchId_reserva,
                searchMatricula);
    }

    /**
     * Obté una llista de dates en què un vehicle no està disponible per a noves
     * reserves.
     *
     * @param matricula La matrícula del vehicle.
     * @return Una llista de dates no disponibles en format "yyyy-MM-dd".
     */
    public List<String> obtenerFechasNoDisponibles(String matricula) {
        log.info("Obtenint dates no disponibles per al vehicle amb matrícula: {}", matricula);
        List<Reserva> reservas = reservaRepository.findByVehicleMatriculaEstat(matricula);
        List<String> fechasNoDisponibles = new ArrayList<>();

        for (Reserva reserva : reservas) {
            log.debug("Afegint dates de reserva amb ID: {}", reserva.getIdReserva());
            LocalDate fechaInicio = reserva.getDataInici();
            LocalDate fechaFin = reserva.getDataFi();

            while (!fechaInicio.isAfter(fechaFin)) {
                fechasNoDisponibles.add(fechaInicio.toString());
                fechaInicio = fechaInicio.plusDays(1);
            }
        }

        return fechasNoDisponibles;
    }
}
