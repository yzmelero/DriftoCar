/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Incidencia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 *
 * @author Anna
 */
@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {

    List<Incidencia> findByEstat(boolean estat);

    @Query("SELECT i FROM Incidencia i "
            + "LEFT JOIN i.matricula v "
            + "LEFT JOIN v.localitzacio l "
            + "WHERE (:matricula IS NULL OR v.matricula = :matricula) "
            + "AND (:codiPostal IS NULL OR l.codiPostal = :codiPostal) "
            + "AND (:estat IS NULL OR i.estat = :estat)")
    List<Incidencia> findByFiltres(
            @Param("matricula") String matricula,
            @Param("codiPostal") String codiPostal,
            @Param("estat") Boolean estat
    );

}
