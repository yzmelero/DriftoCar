/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Client;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;



/**
 *
 * @author Anna
 */
public interface ClientRepository extends JpaRepository <Client,String>  {
    
    //Guarda una instància de client.
    //Client registreClient(Client client);

    //Retorna un client amb l'ID especificat.
    Optional<Client> findByDni(String dni);
    
    //Modifica un client.
    //Client modificarClient(Client client, String dni);
    
    //void esborrarClient(String id);
    
   
    
}
