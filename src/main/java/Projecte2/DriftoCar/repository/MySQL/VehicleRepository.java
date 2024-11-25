/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MySQL;

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

    List<Vehicle> findByLocalitzacioCodiPostal(String codiPostal);

    List<Vehicle> findByTipusAndDisponibilitat(TipusVehicle tipus, boolean disponibilitat);

    List<Vehicle> findByAnyBetween(int startYear, int endYear);

}
