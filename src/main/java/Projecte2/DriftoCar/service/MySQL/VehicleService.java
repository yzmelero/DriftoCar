/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MySQL.LocalitzacioRepository;
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

    public Vehicle modificaVehicle(Vehicle vehicleActualitzat) {
        Optional<Vehicle> vehicleExistent = vehicleRepository.findByMatricula(vehicleActualitzat.getMatricula());

        if (vehicleExistent.isEmpty()) {
            throw new RuntimeException("No existeix cap vehicle amb la matrícula: " + vehicleActualitzat.getMatricula());
        }
        Vehicle vehicleAntic = vehicleExistent.get();

        Optional<Localitzacio> localitzacio = localitzacioRepository.findById(vehicleActualitzat.getLocalitzacio().getCodiPostal());
        if (localitzacio.isEmpty()) {
            throw new RuntimeException("No existeix la localitzacio inserida.");
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

        return vehicleRepository.save(vehicleAntic);
    }

    public List<Vehicle> llistarVehicles() {
        return vehicleRepository.findAll();
    }
}
