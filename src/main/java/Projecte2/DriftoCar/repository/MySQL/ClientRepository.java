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
 *
 * @author Anna
 */
@Repository
public interface ClientRepository extends JpaRepository<Client, String> {

        //Lista todos los clientes por validar
        List<Client> findByActivoFalse();


        // Guarda una instància de client.
        // Client registreClient(Client client);

        // Retorna un client amb l'ID especificat.
        List<Client> findAll();

        Optional<Client> findByDni(String dni);

        // Mètode necessàri per al filtre en la pantalla de llistar clients.
        /*
         * List<Client>
         * findByNomContainingIgnoreCaseOrDniContainingIgnoreCaseOrEmailContainingIgnoreCase(
         * String nom, String dni, String email);
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

        Optional<Client> findByUsuari(String username);


        Optional<Client> findByTelefon(String telefon);


        Optional<Client> findByEmail(String email);


        Optional<Client> findByNumTarjetaCredit(String numTarjetaCredit);

        // Modifica un client.
        // Client modificarClient(Client client, String dni);

        // void esborrarClient(String id);

}
