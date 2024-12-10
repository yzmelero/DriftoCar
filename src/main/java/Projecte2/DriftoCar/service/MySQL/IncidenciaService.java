/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Incidencia;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MySQL.IncidenciaRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;
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
        // Buscar el vehículo por matrícula usando Optional
        Optional<Vehicle> optionalVehicle = vehicleRepository.findByMatricula(incidencia.getMatricula().getMatricula());

        // Verificar si el vehículo está presente
        if (!optionalVehicle.isPresent()) {
            throw new RuntimeException("Vehicle no trobat amb la matrícula proporcionada: " + incidencia.getMatricula().getMatricula());
        }

        // Obtener el vehículo de Optional
        Vehicle vehicle = optionalVehicle.get();

        // Guardar la nueva incidencia
        incidencia.setEstat(true); // Estat = oberta
        incidenciaRepository.save(incidencia); // Guardamos la incidencia en la base de datos

        // Si la disponibilidad del vehículo ha cambiado, actualizarla
        if (incidencia.getMatricula().isDisponibilitat() != vehicle.isDisponibilitat()) {
            vehicle.setDisponibilitat(incidencia.getMatricula().isDisponibilitat());
            vehicleRepository.save(vehicle); // Guardamos el vehículo con su nueva disponibilidad
        }
    }
}


