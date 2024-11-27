/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MySQL.LocalitzacioRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Anna
 */
@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private LocalitzacioRepository localitzacioRepository;

    public Vehicle altaVehicle(Vehicle vehicle) {

        if (vehicleRepository.existsById(vehicle.getMatricula())) {
            throw new RuntimeException("Ja existeix un vehícle amb la matrícula: " + vehicle.getMatricula());
        }
        return vehicleRepository.save(vehicle);
    }

    public void baixaVehicle(String matricula) {
        if (!vehicleRepository.existsById(matricula)) {
            throw new RuntimeException("No existeix cap vehicle amb la matrícula: " + matricula);
        }
        vehicleRepository.deleteById(matricula);
    }

    public Vehicle modificaVehicle(String matricula, Vehicle vehicleActualitzat) {
        Vehicle vehicleExistent = vehicleRepository.findByMatricula(matricula).get();

        if (vehicleExistent == null) {
            throw new RuntimeException("No existeix cap vehicle amb la matrícula: " + matricula);
        }

        Localitzacio localitzacio = localitzacioRepository.findById(vehicleActualitzat.getLocalitzacio().getCodiPostal()).orElse(null);

        if (localitzacio != null) {
            vehicleExistent.setLocalitzacio(localitzacio);
        }

        vehicleExistent.setMarca(vehicleActualitzat.getMarca());
        vehicleExistent.setModel(vehicleActualitzat.getModel());
        vehicleExistent.setAny(vehicleActualitzat.getAny());
        vehicleExistent.setPlaces(vehicleActualitzat.getPlaces());
        vehicleExistent.setTransmisio(vehicleActualitzat.getTransmisio());
        vehicleExistent.setCombustible(vehicleActualitzat.getCombustible());
        vehicleExistent.setTipus(vehicleActualitzat.getTipus());
        vehicleExistent.setDisponibilitat(vehicleActualitzat.isDisponibilitat());

        return vehicleRepository.save(vehicleExistent);
    }

}
