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

    // Guarda una instància de client.
    // Client registreClient(Client client);

    // Retorna un client amb l'ID especificat.
    // List<Client> findAll();
    Optional<Client> findByDni(String dni);

    // Mètode necessàri per al filtre en la pantalla de llistar clients.
    List<Client> findByNomContainingIgnoreCaseOrDniContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String nom, String dni, String email);

    @Query("SELECT c FROM Client c " +
            "WHERE (:nom IS NULL OR LOWER(c.nom) LIKE LOWER(CONCAT('%', :nom, '%'))) " +
            "AND (:dni IS NULL OR LOWER(c.dni) LIKE LOWER(CONCAT('%', :dni, '%'))) " +
            "AND (:email IS NULL OR LOWER(c.email) LIKE LOWER(CONCAT('%', :email, '%')))")
    List<Client> cercarClients(@Param("nom") String nom,
            @Param("dni") String dni,
            @Param("email") String email);
    // Modifica un client.
    // Client modificarClient(Client client, String dni);

    // void esborrarClient(String id);

}
