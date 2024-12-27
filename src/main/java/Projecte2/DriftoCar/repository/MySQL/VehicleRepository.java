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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Anna
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

  Optional<Vehicle> findByMatricula(String matricula);

  List<Vehicle> findByMatriculaContaining(String matricula);

  List<Vehicle> findByMarca(String marca);

  List<Vehicle> findByModel(String model);

  List<Vehicle> findByDisponibilitat(boolean disponibilitat);

  List<Vehicle> findByLocalitzacio(Localitzacio localitzacio);

  List<Vehicle> findByTipus(TipusVehicle tipus);

  List<Vehicle> findByCombustible(TipusCombustible combustible);

  List<Vehicle> findByPlaces(int places);

  List<Vehicle> findByTransmisio(TipusTransmisio transmisio);

  List<Vehicle> findByAny(int any);

  List<Vehicle> findByLocalitzacio_CodiPostal(String codiPostal);

  @Query("""
      SELECT v FROM Vehicle v
      WHERE (:matricula IS NULL OR LOWER(v.matricula) LIKE LOWER(CONCAT('%', :matricula, '%')))
        AND NOT EXISTS (
            SELECT 1
            FROM Reserva r
            WHERE r.vehicle.matricula = v.matricula
              AND r.estat = true
              AND ((:dataInici IS NOT NULL AND :dataFinal IS NOT NULL AND r.dataInici <= :dataFinal AND r.dataFi >= :dataInici)
                   OR (:dataInici IS NOT NULL AND :dataFinal IS NULL AND r.dataFi >= :dataInici)
                   OR (:dataInici IS NULL AND :dataFinal IS NOT NULL AND r.dataInici <= :dataFinal))
        )
      """)
  List<Vehicle> findVehiclesLista(@Param("dataInici") LocalDate dataInici,
      @Param("dataFinal") LocalDate dataFinal,
      @Param("matricula") String matricula);

  @Query("""
      SELECT v FROM Vehicle v
      WHERE (LOWER(v.matricula) LIKE LOWER(CONCAT('%', :matricula, '%')) OR :matricula IS NULL)
        AND v.matricula NOT IN (
          SELECT i.matricula.matricula
          FROM Incidencia i
          WHERE i.estat = true
        )
      """)
  List<Vehicle> findVehiclesFiltreIncidencies(@Param("matricula") String matricula);

}
