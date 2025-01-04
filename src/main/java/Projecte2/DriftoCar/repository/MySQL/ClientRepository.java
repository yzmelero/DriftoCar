/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Client;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repositori per gestionar les operacions CRUD de la taula `Client` a la base
 * de dades relacional.
 * Extén {@link JpaRepository} per proporcionar funcionalitats predefinides.
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

        /**
         * Cerca tots els clients que encara no han estat activats.
         *
         * @return una llista de clients no activats.
         */
        List<Client> findByActivoFalse();

        // Guarda una instància de client.
        // Client registreClient(Client client);

        /**
         * Retorna tots els clients de la base de dades.
         *
         * @return una llista de clients.
         */
        List<Client> findAll();

        /**
         * Cerca un client pel seu DNI.
         *
         * @param dni el DNI del client.
         * @return un {@link Optional} que conté el client si es troba, buit en cas
         *         contrari.
         */
        Optional<Client> findByDni(String dni);

        /**
         * Cerca clients segons els criteris especificats.
         *
         * @param cognoms      els cognoms del client (pot ser null).
         * @param nacionalitat la nacionalitat del client (pot ser null).
         * @param telefon      el número de telèfon del client (pot ser null).
         * @param email        el correu electrònic del client (pot ser null).
         * @return una llista de clients que coincideixin amb els criteris.
         */
        @Query("SELECT c FROM Client c " +
                        "WHERE (:cognoms IS NULL OR LOWER(c.cognoms) LIKE LOWER(CONCAT('%', :cognoms, '%'))) " +
                        "AND (:nacionalitat IS NULL OR LOWER(c.nacionalitat) LIKE LOWER(CONCAT('%', :nacionalitat, '%'))) "
                        +
                        "AND (:telefon IS NULL OR (c.telefon) LIKE (CONCAT('%', :telefon, '%')))" +
                        "AND (:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%')))")
        List<Client> cercarClients(@Param("cognoms") String cognoms,
                        @Param("nacionalitat") String nacionalitat,
                        @Param("telefon") String telefon,
                        @Param("email") String email);

        /**
         * Cerca un client pel seu nom d'usuari.
         *
         * @param username el nom d'usuari del client.
         * @return un {@link Optional} que conté el client si es troba, buit en cas
         *         contrari.
         */
        Optional<Client> findByUsuari(String username);

        /**
         * Cerca un client pel seu número de telèfon.
         *
         * @param telefon el número de telèfon del client.
         * @return un {@link Optional} que conté el client si es troba, buit en cas
         *         contrari.
         */
        Optional<Client> findByTelefon(String telefon);

        /**
         * Cerca un client pel seu correu electrònic.
         *
         * @param email el correu electrònic del client.
         * @return un {@link Optional} que conté el client si es troba, buit en cas
         *         contrari.
         */
        Optional<Client> findByEmail(String email);

        /**
         * Cerca un client pel seu número de targeta de crèdit.
         *
         * @param numTarjetaCredit el número de targeta de crèdit del client.
         * @return un {@link Optional} que conté el client si es troba, buit en cas
         *         contrari.
         */
        Optional<Client> findByNumTarjetaCredit(String numTarjetaCredit);

        // Modifica un client.
        // Client modificarClient(Client client, String dni);

        // void esborrarClient(String id);

}
