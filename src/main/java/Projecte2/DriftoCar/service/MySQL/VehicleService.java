/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Vehicle;
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

    public Vehicle altaVehicle(Vehicle vehicle) {
        
        if (vehicleRepository.existsById(vehicle.getMatricula())) {
            throw new RuntimeException("Ja existeix un vehícle amb la matrícula: " + vehicle.getMatricula());
        }
        return vehicleRepository.save(vehicle);
    }
}
