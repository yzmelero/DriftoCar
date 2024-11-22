/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MySQL;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import Projecte2.DriftoCar.entity.MySQL.Client;

/**
 *
 * @author Anna
 */
public interface ClientRepository extends JpaRepository<Client, String>{

    Optional<Client> findByDni(String dni);
    
}
