/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MySQL.LocalitzacioRepository;
import Projecte2.DriftoCar.repository.MySQL.ReservaRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servei que gestiona les operacions relacionades amb els vehicles.
 * Proporciona funcionalitats per donar d'alta, modificar, desactivar o activar
 * vehicles,
 * així com llistar i obtenir informació específica.
 */
@Service
public class VehicleService {

    Logger log = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private LocalitzacioRepository localitzacioRepository;

    @Autowired
    private ReservaRepository reservaRepository;

    /**
     * Dona d'alta un nou vehicle si no existeix ja al sistema.
     *
     * @param vehicle El vehicle a afegir.
     * @return El vehicle afegit.
     * @throws RuntimeException Si ja existeix un vehicle amb la mateixa matrícula.
     */
    public Vehicle altaVehicle(Vehicle vehicle) {
        log.info("S'ha entrat al metode d'altaVehicle del servei.");

        if (vehicleRepository.existsById(vehicle.getMatricula())) {
            log.warn("Ja existeix un vehicle amb la matrícula: {}", vehicle.getMatricula());
            throw new RuntimeException("Ja existeix un vehícle amb la matrícula: " + vehicle.getMatricula());
        }
        log.info("Vehicle amb matrícula {} afegit correctament.", vehicle.getMatricula());
        return vehicleRepository.save(vehicle);
    }

    /**
     * Dona de baixa un vehicle si no té reserves actives.
     *
     * @param matricula La matrícula del vehicle a eliminar.
     * @throws RuntimeException Si no existeix el vehicle o té reserves actives.
     */
    public void baixaVehicle(String matricula) {
        log.info("S'ha entrat al metode de baixaReserva del servei.");
        if (!vehicleRepository.existsById(matricula)) {
            log.warn("No existeix cap vehicle amb la matrícula: {}", matricula);
            throw new RuntimeException("No existeix cap vehicle amb la matrícula: " + matricula);
        }
        boolean tieneReservas = reservaRepository.existsByVehicleMatricula(matricula);

        if (tieneReservas) {
            log.warn("El vehicle amb matrícula {} té reserves actives i no es pot eliminar.", matricula);
            throw new RuntimeException("No es pot esborrar un vehicle amb reserves actives.");
        }
        vehicleRepository.deleteById(matricula);
        log.info("Vehicle amb matrícula {} eliminat correctament.", matricula);
    }

    /**
     * Modifica les dades d'un vehicle existent.
     *
     * @param vehicleActualitzat El vehicle amb les dades actualitzades.
     * @return El vehicle modificat.
     * @throws RuntimeException Si el vehicle o la localització associada no
     *                          existeixen.
     */
    public Vehicle modificaVehicle(Vehicle vehicleActualitzat) {
        log.info("S'ha entrat al metode de modificaVehicle del servei .");

        Optional<Vehicle> vehicleExistent = vehicleRepository.findByMatricula(vehicleActualitzat.getMatricula());

        if (vehicleExistent.isEmpty()) {
            log.warn("No s'ha trobat cap vehicle amb la matrícula: {}", vehicleActualitzat.getMatricula());
            throw new RuntimeException(
                    "No existeix cap vehicle amb la matrícula: " + vehicleActualitzat.getMatricula());
        }
        Vehicle vehicleAntic = vehicleExistent.get();

        Optional<Localitzacio> localitzacio = localitzacioRepository
                .findById(vehicleActualitzat.getLocalitzacio().getCodiPostal());
        if (localitzacio.isEmpty()) {
            log.warn("La localització amb codi postal {} no existeix.",
                    vehicleActualitzat.getLocalitzacio().getCodiPostal());
            throw new RuntimeException("No existeix la localitzacio inserida.");
        }

        if (vehicleActualitzat.getImatge() != null && vehicleActualitzat.getImatge().length > 0) {
            vehicleAntic.setImatge(vehicleActualitzat.getImatge());
        }

        vehicleAntic.setLocalitzacio(vehicleActualitzat.getLocalitzacio());
        vehicleAntic.setMarca(vehicleActualitzat.getMarca());
        vehicleAntic.setModel(vehicleActualitzat.getModel());
        vehicleAntic.setAny(vehicleActualitzat.getAny());
        vehicleAntic.setPlaces(vehicleActualitzat.getPlaces());
        vehicleAntic.setTransmisio(vehicleActualitzat.getTransmisio());
        vehicleAntic.setCombustible(vehicleActualitzat.getCombustible());
        vehicleAntic.setTipus(vehicleActualitzat.getTipus());
        vehicleAntic.setDisponibilitat(vehicleActualitzat.isDisponibilitat());
        vehicleAntic.setCostHora(vehicleActualitzat.getCostHora());
        vehicleAntic.setKmMax(vehicleActualitzat.getKmMax());
        vehicleAntic.setFianca(vehicleActualitzat.getFianca());
        vehicleAntic.setMotiu(vehicleActualitzat.getMotiu());
        vehicleAntic.setImporte(vehicleActualitzat.getImporte());

        log.info("Vehicle amb matrícula {} modificat correctament.", vehicleActualitzat.getMatricula());
        return vehicleRepository.save(vehicleAntic);
    }

    /**
     * Llista tots els vehicles disponibles al sistema.
     *
     * @return Una llista de vehicles.
     */
    public List<Vehicle> llistarVehicles() {
        log.info("S'ha entrat al mètode de llistarVehicles del servei.");
        return vehicleRepository.findAll();
    }

    /**
     * Obté un vehicle per la seva matrícula.
     *
     * @param matricula La matrícula del vehicle a cercar.
     * @return El vehicle corresponent, o null si no existeix.
     */
    public Vehicle obtenirVehicleMatricula(String matricula) {
        log.info("S'ha entrat al mètode d'obtenirVehicleMatricula del servei per a la matrícula: {}", matricula);
        return vehicleRepository.findByMatricula(matricula).orElse(null);
    }

    /*
     * public List<Vehicle> getVehiclesDisponibles(LocalDate dataInici, LocalDate
     * dataFinal) {
     * return vehicleRepository.findVehiclesDisponibles(dataInici, dataFinal);
     * }
     */

    /**
     * Desactiva un vehicle per fer-lo no disponible.
     *
     * @param matricula La matrícula del vehicle a desactivar.
     * @throws RuntimeException Si el vehicle no existeix.
     */
    public void desactivarVehicle(String matricula) {
        log.info("S'ha entrat al mètode de desactivarVehicle del servei per al vehicle amb matrícula: {}", matricula);
        Vehicle vehicle = vehicleRepository.findById(matricula)
                .orElseThrow(() -> new RuntimeException("Vehicle no trobat amb matrícula: " + matricula));
        vehicle.setDisponibilitat(false);
        vehicleRepository.save(vehicle);
        log.info("Vehicle amb matrícula {} desactivat correctament.", matricula);
    }

    /**
     * Activa un vehicle per fer-lo disponible.
     *
     * @param matricula La matrícula del vehicle a activar.
     * @throws RuntimeException Si el vehicle no existeix.
     */
    public void activarVehicle(String matricula) {
        log.info("S'ha entrat al mètode d'activarVehicle del servei per al vehicle amb matrícula: {}", matricula);
        Vehicle vehicle = vehicleRepository.findById(matricula)
                .orElseThrow(() -> new RuntimeException("Vehicle no trobat amb matrícula: " + matricula));
        vehicle.setDisponibilitat(true);
        vehicleRepository.save(vehicle);
        log.info("Vehicle amb matrícula {} activat correctament.", matricula);
    }

    /**
     * Cerca vehicles disponibles en un rang de dates o amb matrícula específica.
     *
     * @param dataInici La data inicial.
     * @param dataFinal La data final.
     * @param matricula La matrícula del vehicle (opcional).
     * @return Una llista de vehicles disponibles.
     */
    public List<Vehicle> findVehiclesLista(LocalDate dataInici, LocalDate dataFinal, String matricula) {
        log.info("Cercant vehicles disponibles del {} al {} amb matrícula: {}", dataInici, dataFinal, matricula);
        return vehicleRepository.findVehiclesLista(dataInici, dataFinal, matricula);
    }
}
