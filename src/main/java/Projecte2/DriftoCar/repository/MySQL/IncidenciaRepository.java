/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Incidencia;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
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
}