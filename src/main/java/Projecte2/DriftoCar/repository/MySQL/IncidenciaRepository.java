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
 * Repositori per gestionar les operacions CRUD de la taula `Incidencia` a la
 * base de dades relacional.
 * Extén {@link JpaRepository} per proporcionar funcionalitats predefinides.
 */
@Repository
public interface IncidenciaRepository extends JpaRepository<Incidencia, Long> {
    /**
     * Cerca una llista d'incidències segons el seu estat.
     *
     * @param estat l'estat de l'incidència (true = actiu, false = inactiu).
     * @return una llista d'incidències amb l'estat especificat.
     */
    List<Incidencia> findByEstat(boolean estat);

    /**
     * Cerca incidències per diversos filtres opcionals.
     *
     * @param matricula  la matrícula del vehicle (opcional).
     * @param codiPostal el codi postal de la localització (opcional).
     * @param estat      l'estat de la incidència (opcional).
     * @return una llista d'incidències que compleixin els filtres especificats.
     */
    @Query("SELECT i FROM Incidencia i "
            + "LEFT JOIN i.matricula v "
            + "LEFT JOIN v.localitzacio l "
            + "WHERE (:matricula IS NULL OR LOWER(v.matricula) LIKE LOWER(CONCAT('%',:matricula, '%'))) "
            + "AND (:codiPostal IS NULL OR l.codiPostal = :codiPostal) "
            + "AND (:estat IS NULL OR i.estat = :estat)")
    List<Incidencia> findByFiltres(
            @Param("matricula") String matricula,
            @Param("codiPostal") String codiPostal,
            @Param("estat") Boolean estat);

}
