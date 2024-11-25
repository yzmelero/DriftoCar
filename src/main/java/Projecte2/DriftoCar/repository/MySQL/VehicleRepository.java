/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.entity.MySQL.TipusCombustible;
import Projecte2.DriftoCar.entity.MySQL.TipusTransmisio;
import Projecte2.DriftoCar.entity.MySQL.TipusVehicle;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Anna
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    Optional<Vehicle> findByMatricula(String matricula);

    List<Vehicle> findByMarca(String marca);

    List<Vehicle> findByModel(String model);

    List<Vehicle> findByDisponibilitat(boolean disponibilitat);

    List<Vehicle> findByLocalitzacio(Localitzacio localitzacio);

    List<Vehicle> findByTipus(TipusVehicle tipus);
    
    List<Vehicle> findByCombustible(TipusCombustible combustible);
    
    List<Vehicle> findByPlaces (int places);

    List<Vehicle> findByTransmisio(TipusTransmisio transmisio);

    List<Vehicle> findByAny(int any);

}
