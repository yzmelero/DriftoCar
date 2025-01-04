/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositori per gestionar les operacions CRUD de la taula `Localitzacio` a la
 * base de dades relacional.
 * Extén {@link JpaRepository} per proporcionar funcionalitats predefinides.
 */
@Repository
public interface LocalitzacioRepository extends JpaRepository<Localitzacio, String> {
    /**
     * Cerca totes les localitzacions que no tenen un agent assignat.
     *
     * @return una llista de localitzacions sense agent.
     */
    List<Localitzacio> findByAgentIsNull();
}
