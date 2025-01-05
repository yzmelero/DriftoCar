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
 * Repositori per gestionar les operacions CRUD de la taula `Vehicle` a la base
 * de dades relacional.
 * Extén {@link JpaRepository} per proporcionar funcionalitats predefinides.
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, String> {

  /**
   * Cerca un vehicle per matrícula.
   *
   * @param matricula la matrícula del vehicle.
   * @return un {@link Optional} que conté el vehicle si es troba, buit en cas
   *         contrari.
   */
  Optional<Vehicle> findByMatricula(String matricula);

  /**
   * Cerca vehicles que continguin una matrícula específica parcialment.
   *
   * @param matricula una cadena parcial de la matrícula.
   * @return una llista de vehicles que continguin la cadena especificada.
   */
  List<Vehicle> findByMatriculaContaining(String matricula);

  /**
   * Cerca vehicles per la marca especificada.
   *
   * @param marca la marca del vehicle.
   * @return una llista de vehicles que coincideixin amb la marca.
   */
  List<Vehicle> findByMarca(String marca);

  /**
   * Cerca vehicles per el model especificat.
   *
   * @param model el model del vehicle.
   * @return una llista de vehicles que coincideixin amb el model.
   */
  List<Vehicle> findByModel(String model);

  /**
   * Cerca vehicles segons la seva disponibilitat.
   *
   * @param disponibilitat true si el vehicle està disponible, false en cas
   *                       contrari.
   * @return una llista de vehicles segons la disponibilitat.
   */
  List<Vehicle> findByDisponibilitat(boolean disponibilitat);

  /**
   * Cerca vehicles associats a una localització específica.
   *
   * @param localitzacio l'entitat {@link Localitzacio} amb la qual està associat
   *                     el vehicle.
   * @return una llista de vehicles associats a la localització.
   */
  List<Vehicle> findByLocalitzacio(Localitzacio localitzacio);

  /**
   * Cerca vehicles per tipus.
   *
   * @param tipus l'enumeració {@link TipusVehicle} que defineix el tipus del
   *              vehicle.
   * @return una llista de vehicles que coincideixin amb el tipus.
   */
  List<Vehicle> findByTipus(TipusVehicle tipus);

  /**
   * Cerca vehicles segons el tipus de combustible.
   *
   * @param combustible l'enumeració {@link TipusCombustible} que defineix el
   *                    tipus de combustible.
   * @return una llista de vehicles que coincideixin amb el combustible.
   */
  List<Vehicle> findByCombustible(TipusCombustible combustible);

  /**
   * Cerca vehicles segons el nombre de places.
   *
   * @param places el nombre de places del vehicle.
   * @return una llista de vehicles que tinguin el nombre especificat de places.
   */
  List<Vehicle> findByPlaces(int places);

  /**
   * Cerca vehicles segons el tipus de transmissió.
   *
   * @param transmisio l'enumeració {@link TipusTransmisio} que defineix el tipus
   *                   de transmissió.
   * @return una llista de vehicles que coincideixin amb el tipus de transmissió.
   */
  List<Vehicle> findByTransmisio(TipusTransmisio transmisio);

  /**
   * Cerca vehicles per l'any de fabricació.
   *
   * @param any l'any de fabricació del vehicle.
   * @return una llista de vehicles fabricats en l'any especificat.
   */
  List<Vehicle> findByAny(int any);

  /**
   * Cerca vehicles per codi postal de la localització.
   *
   * @param codiPostal el codi postal de la localització associada.
   * @return una llista de vehicles associats a la localització amb el codi postal
   *         especificat.
   */
  List<Vehicle> findByLocalitzacio_CodiPostal(String codiPostal);

  /**
   * Cerca vehicles disponibles segons la matrícula, excloent aquells amb reserves
   * actives dins d'un període de temps.
   *
   * @param dataInici la data d'inici del període (opcional).
   * @param dataFinal la data final del període (opcional).
   * @param matricula la matrícula del vehicle (opcional).
   * @return una llista de vehicles que compleixin els criteris.
   */
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

  /**
   * Cerca vehicles segons la matrícula i excloent aquells amb incidències
   * actives.
   *
   * @param matricula la matrícula del vehicle (opcional).
   * @return una llista de vehicles que compleixin els criteris.
   */
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

  /**
   * Cerca matrícules de vehicles gestionats per un agent segons el seu usuari.
   *
   * @param usuari el nom d'usuari de l'agent.
   * @return una llista de matrícules gestionades per l'agent.
   */
  @Query("""
          SELECT v.matricula
          FROM Vehicle v
          WHERE v.localitzacio.id = (
              SELECT a.localitzacio.id
              FROM Agent a
              WHERE a.usuari = :usuari
          )
      """)
  List<String> findMatriculesByAgentUsuari(String usuari);

}
