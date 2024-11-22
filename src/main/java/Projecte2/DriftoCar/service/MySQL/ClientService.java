/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.repository.MySQL.ClientRepository;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *
 * @author Anna
 */
@Service
public class ClientService {
    
    @Autowired
    ClientRepository clientRepository;
    
    Logger log = LoggerFactory.getLogger(ClientService.class);
    public ClientService(ClientRepository clientRepository){
        
        this.clientRepository = clientRepository;
        
    }
    
    public Client registreClient(Client client){
        log.info("S'ha entrat al mètode registreClient");
        
        if (client.getUsuari() == null || client.getUsuari().isEmpty()) {
            throw new IllegalArgumentException("El camp usuari no pot estar buit.");
        }
        if (client.getEmail() == null || client.getEmail().isEmpty()) {
            throw new IllegalArgumentException("El camp email no pot estar buit.");
        }
        if (client.getContrasenya() == null || client.getContrasenya().isEmpty()) {
            throw new IllegalArgumentException("El camp contrasenya no pot estar buit.");
        }
        
        
        
        //Comprovar si ja existeix un client amb el nom d'usuari inserit.
        Optional<Client> clientExistent = clientRepository.findByDni(client.getDni());
        
        return clientRepository.save(client);
        
}
}
