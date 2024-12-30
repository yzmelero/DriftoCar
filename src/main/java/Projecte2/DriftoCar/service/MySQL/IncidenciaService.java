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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Anna
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

    public List<Vehicle> llistarVehiclesSenseIncidenciesActives() {
        List<Incidencia> incidenciesActives = incidenciaRepository.findByEstat(true);

        List<String> matriculesAmbIncidenciesActives = new ArrayList<>();
        for (Incidencia incidencia : incidenciesActives) {
            matriculesAmbIncidenciesActives.add(incidencia.getMatricula().getMatricula());
        }

        List<Vehicle> vehicles = vehicleRepository.findAll();
        List<Vehicle> vehiclesSenseIncidencies = new ArrayList<>();
        for (Vehicle vehicle : vehicles) {
            if (!matriculesAmbIncidenciesActives.contains(vehicle.getMatricula())) {
                vehiclesSenseIncidencies.add(vehicle);
            }
        }

        return vehiclesSenseIncidencies;
    }

    public void obrirIncidencia(Incidencia incidencia) {
        Optional<Vehicle> optionalVehicle = vehicleRepository.findByMatricula(incidencia.getMatricula().getMatricula());

        if (!optionalVehicle.isPresent()) {
            throw new RuntimeException("Vehicle no trobat amb la matrícula proporcionada: " + incidencia.getMatricula().getMatricula());
        }

        Vehicle vehicle = optionalVehicle.get();

        incidencia.setEstat(true);
        incidenciaRepository.save(incidencia);

        if (incidencia.getMatricula().isDisponibilitat() != vehicle.isDisponibilitat()) {
            vehicle.setDisponibilitat(incidencia.getMatricula().isDisponibilitat());
            vehicleRepository.save(vehicle);
        }
    }

    public void tancarIncidencia(Long id) {
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
    }

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
    

    public List<Incidencia> llistarIncidencies() {
        return incidenciaRepository.findAll();
    }

    public Incidencia obtenirIncidenciaPerId(Long id) {
        return incidenciaRepository.findById(id).orElse(null);
    }
}