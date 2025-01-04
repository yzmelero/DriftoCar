/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MySQL;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Projecte2.DriftoCar.entity.MySQL.Agent;
import Projecte2.DriftoCar.entity.MySQL.Localitzacio;

/**
 * Repositori per gestionar les operacions CRUD de la taula `Agent` a la base de dades relacional.
 * Extén {@link JpaRepository} per proporcionar funcionalitats predefinides.
 */
@Repository
public interface AgentRepository extends JpaRepository<Agent, String> {

    /**
     * Cerca una llista d'agents amb un DNI que contingui la cadena especificada.
     *
     * @param dni la cadena parcial o completa del DNI a cercar.
     * @return una llista d'agents que coincideixin amb el criteri.
     */
    List<Agent> findByDniContaining(String dni);

    /**
     * Verifica si existeix un agent associat a una localització específica.
     *
     * @param localitzacio la localització a verificar.
     * @return true si existeix un agent amb aquesta localització, false en cas contrari.
     */
    boolean existsByLocalitzacio(Localitzacio localitzacio);

    /**
     * Cerca un agent pel seu correu electrònic.
     *
     * @param email el correu electrònic de l'agent.
     * @return un {@link Optional} que conté l'agent si es troba, buit en cas contrari.
     */
    Optional<Agent> findByEmail(String email);

    /**
     * Cerca un agent pel seu nom d'usuari.
     *
     * @param usuari el nom d'usuari de l'agent.
     * @return un {@link Optional} que conté l'agent si es troba, buit en cas contrari.
     */
    Optional<Agent> findByUsuari(String usuari);

    /**
     * Cerca un agent pel seu número de targeta de crèdit.
     *
     * @param numTarjetaCredit el número de targeta de crèdit de l'agent.
     * @return un {@link Optional} que conté l'agent si es troba, buit en cas contrari.
     */
    Optional<Agent> findByNumTarjetaCredit(String numTarjetaCredit);

    /**
     * Cerca un agent pel seu número de telèfon.
     *
     * @param telefon el número de telèfon de l'agent.
     * @return un {@link Optional} que conté l'agent si es troba, buit en cas contrari.
     */
    Optional<Agent> findByTelefon(String telefon);

}
