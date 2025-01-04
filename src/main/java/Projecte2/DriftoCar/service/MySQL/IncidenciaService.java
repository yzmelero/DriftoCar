/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Incidencia;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MySQL.IncidenciaRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;
import Projecte2.DriftoCar.service.MongoDB.HistoricIncidenciesService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servei per gestionar les incidències relacionades amb els vehicles.
 * Aquest servei permet obrir, tancar i modificar incidències, així com
 * consultar-ne l'estat.
 * També permet consultar vehicles sense incidències actives.
 */
@Service
public class IncidenciaService {

    Logger log = LoggerFactory.getLogger(IncidenciaService.class);

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private HistoricIncidenciesService historicIncidenciesService;

    /**
     * Llista els vehicles que no tenen incidències actives. També permet cercar
     * vehicles per matrícula.
     *
     * @param searchMatricula La matrícula del vehicle a cercar (pot ser parcial).
     * @return Una llista de vehicles sense incidències actives.
     */
    public List<Vehicle> llistarVehiclesSenseIncidenciesActives(String searchMatricula) {
        log.info("S'ha entrat al mètode llistarVehiclesSenseIncidenciesActives del servei.");

        List<Vehicle> vehicles = vehicleRepository.findVehiclesFiltreIncidencies(searchMatricula);
        return vehicles;
    }

    /**
     * Obre una nova incidència per un vehicle especificat. Si la matrícula del
     * vehicle no existeix,
     * es llença una excepció.
     *
     * @param incidencia La incidència que es vol obrir.
     */
    public void obrirIncidencia(Incidencia incidencia) {
        log.info("S'ha entrat al mètode obrirIncidencia del servei per la matrícula: {}",
                incidencia.getMatricula().getMatricula());

        Optional<Vehicle> optionalVehicle = vehicleRepository.findByMatricula(incidencia.getMatricula().getMatricula());

        if (!optionalVehicle.isPresent()) {
            throw new RuntimeException(
                    "Vehicle no trobat amb la matrícula proporcionada: " + incidencia.getMatricula().getMatricula());
        }

        Vehicle vehicle = optionalVehicle.get();

        incidencia.setEstat(true);
        incidenciaRepository.save(incidencia);

        if (incidencia.getMatricula().isDisponibilitat() != vehicle.isDisponibilitat()) {
            vehicle.setDisponibilitat(incidencia.getMatricula().isDisponibilitat());
            vehicleRepository.save(vehicle);
        }
        log.info("S'ha obert la incidència per la matrícula: {}", incidencia.getMatricula().getMatricula());
    }

    /**
     * Tanca una incidència per l'ID especificat. Si no es troba la incidència, es
     * llença una excepció.
     * També guarda la incidència tancada a l'històric.
     *
     * @param id L'ID de la incidència que es vol tancar.
     */
    public void tancarIncidencia(Long id) {
        log.info("S'ha entrat al mètode tancarIncidencia per ID: {}", id);

        // Buscar la incidencia por ID
        Optional<Incidencia> incidenciaOpt = incidenciaRepository.findById(id);

        if (!incidenciaOpt.isPresent()) {
            throw new RuntimeException("Incidència no trobada amb l'ID: " + id);
        }

        // Obtener la incidencia
        Incidencia incidencia = incidenciaOpt.get();
        incidencia.setEstat(false);
        incidencia.setDataFiIncidencia(LocalDateTime.now());
        incidenciaRepository.save(incidencia);

        historicIncidenciesService.guardarHistoricIncidenciaTancada(incidencia);
        log.info("S'ha tancat la incidència amb ID: {}", id);
    }

    /**
     * Modifica una incidència existent. Actualitza només el motiu de la incidència.
     *
     * @param incidencia La incidència amb les dades a modificar.
     * @return La incidència actualitzada.
     */
    public Incidencia modificarIncidencia(Incidencia incidencia) {
        log.info("S'ha entrat al mètode modificarIncidencia");

        // Obtenir la incidència existent
        Optional<Incidencia> incidenciaExistent = incidenciaRepository.findById(incidencia.getId());

        if (incidenciaExistent.isEmpty()) {
            throw new RuntimeException("No existeix cap incidència amb aquest ID.");
        }

        Incidencia incidenciaActualitzada = incidenciaExistent.get();

        // Actualitzar només el camp 'motiu'
        incidenciaActualitzada.setMotiu(incidencia.getMotiu());

        log.info("S'ha modificat el motiu de la incidència amb ID: {}", incidencia.getId());

        return incidenciaRepository.save(incidenciaActualitzada);
    }

    /**
     * Llista totes les incidències registrades.
     *
     * @return Una llista de totes les incidències.
     */
    public List<Incidencia> llistarIncidencies() {
        log.info("S'ha entrat al mètode llistarIncidencies del servei.");
        return incidenciaRepository.findAll();
    }

    /**
     * Obté una incidència per l'ID especificat.
     *
     * @param id L'ID de la incidència a obtenir.
     * @return La incidència trobada o null si no es troba.
     */
    public Incidencia obtenirIncidenciaPerId(Long id) {
        log.info("S'ha entrat al mètode obtenirIncidenciaPerId del servei.");
        return incidenciaRepository.findById(id).orElse(null);
    }

    /**
     * Filtra les incidències segons la matrícula, codi postal i estat
     * proporcionats.
     * Si no es proporciona cap filtre, es retorna totes les incidències.
     *
     * @param matricula  La matrícula del vehicle per filtrar les incidències (pot
     *                   ser nul·la).
     * @param codiPostal El codi postal per filtrar les incidències (pot ser
     *                   nul·la).
     * @param estat      L'estat de la incidència per filtrar (pot ser nul·la).
     * @return Una llista d'incidències que compleixen els filtres proporcionats.
     */
    public List<Incidencia> filtrarIncidencies(String matricula, String codiPostal, Boolean estat) {
        log.info(
                "S'ha entrat al mètode filtrarIncidencies del servei amb filtres: matricula={}, codiPostal={}, estat={}",
                matricula, codiPostal, estat);

        if ((matricula == null || matricula.isEmpty())
                && (codiPostal == null || codiPostal.isEmpty())
                && estat == null) {
            return incidenciaRepository.findAll();
        }

        return incidenciaRepository.findByFiltres(matricula, codiPostal, estat);
    }
}