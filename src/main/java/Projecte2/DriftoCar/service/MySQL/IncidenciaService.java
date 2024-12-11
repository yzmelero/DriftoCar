/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Incidencia;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MySQL.IncidenciaRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Anna
 */
@Service
public class IncidenciaService {

    @Autowired
    private IncidenciaRepository incidenciaRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

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

    public List<Incidencia> llistarIncidencies() {
        return incidenciaRepository.findAll();
    }
}


