/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.repository.MySQL.ClientRepository;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 *
 * @author Anna
 */
@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder; 

    Logger log = LoggerFactory.getLogger(ClientService.class);

    public ClientService(ClientRepository clientRepository) {

        this.clientRepository = clientRepository;

    }

    // TODO añadir encriptacion de pswd
    public Client altaClient(Client client) throws Exception {
        log.info("S'ha entrat al mètode altaClient");

        if (client.getUsuari() == null || client.getUsuari().isEmpty()) {
            throw new IllegalArgumentException("El camp usuari no pot estar buit.");
        }
        if (client.getEmail() == null || client.getEmail().isEmpty()) {
            throw new IllegalArgumentException("El camp email no pot estar buit.");
        }
        if (client.getContrasenya() == null || client.getContrasenya().isEmpty()) {
            throw new IllegalArgumentException("El camp contrasenya no pot estar buit.");
        }
        if (client.getDni() == null || client.getDni().isEmpty()) {
            throw new IllegalArgumentException("El camp DNI no pot estar buit.");
        }
        if (client.getNom() == null || client.getNom().isEmpty()) {
            throw new IllegalArgumentException("El camp nom no pot estar buit.");
        }
        if (client.getCognoms() == null || client.getCognoms().isEmpty()) {
            throw new IllegalArgumentException("El camp cognoms no pot estar buit.");
        }
        if (client.getNacionalitat() == null || client.getNacionalitat().isEmpty()) {
            throw new IllegalArgumentException("El camp nacionalitat no pot estar buit.");
        }
        if (client.getTelefon() == null || client.getTelefon().isEmpty()) {
            throw new IllegalArgumentException("El camp telèfon no pot estar buit.");
        }
        if (client.getLlicencia() == null || client.getLlicencia().isEmpty()) {
            throw new IllegalArgumentException("El camp llicència no pot estar buit.");
        }
        if (client.getLlicCaducitat() == null || client.getLlicCaducitat().toString().isEmpty()) {
            throw new IllegalArgumentException("El camp caducitat de la llicència no pot estar buit.");
        }
        if (client.getDniCaducitat() == null || client.getDniCaducitat().toString().isEmpty()) {
            throw new IllegalArgumentException("El camp caducitat del dni no pot estar buit.");
        }
        if (client.getNumTarjetaCredit() == null || client.getNumTarjetaCredit().toString().isEmpty()) {
            throw new IllegalArgumentException("El camp tarjeta de crèdit no pot estar buit.");
        }
        if (client.getAdreca() == null || client.getAdreca().isEmpty()) {
            throw new IllegalArgumentException("El camp adreça no pot estar buit.");
        }

        // Comprovar si ja existeix un client amb el dni inserit.
        Optional<Client> clientExistent = clientRepository.findByDni(client.getDni());

        if (clientExistent.isPresent()) {
            throw new Exception("Ja existeix un client amb aquest DNI.");
        }
        log.info("S'ha entrat donat d'alta a un client.");

        String contrasenyaEncriptada = passwordEncoder.encode(client.getContrasenya());
        client.setContrasenya(contrasenyaEncriptada);

        log.info("S'ha encriptat la contrasenya");

        return clientRepository.save(client);

    }

    public Client modificarClient(Client client) {

        log.info("S'ha entrat al mètode modificarClient");

        Optional<Client> clientExistent = clientRepository.findByDni(client.getDni());

        if (clientExistent.isEmpty()) {
            throw new RuntimeException("No existeix cap client amb aquest DNI.");
        }

        // Amb aquesta línia recuperem el client que ja existeix per a poder-lo
        // modificar.
        Client clientNou = clientExistent.get();

        // Loggers per a corregir un bug.
        log.info("Client rebut: {}", client);
        log.info("Telèfon rebut: {}", client.getTelefon());
        log.info("Nacionalitat rebuda: {}", client.getNacionalitat());

        clientNou.setNom(client.getNom());
        clientNou.setCognoms(client.getCognoms());
        clientNou.setLlicencia(client.getLlicencia());
        clientNou.setLlicCaducitat(client.getLlicCaducitat());
        clientNou.setDniCaducitat(client.getDniCaducitat());
        clientNou.setNumTarjetaCredit(client.getNumTarjetaCredit());
        clientNou.setAdreca(client.getAdreca());
        clientNou.setEmail(client.getEmail());
        clientNou.setNacionalitat(client.getNacionalitat());
        clientNou.setTelefon(client.getTelefon());
        clientNou.setContrasenya(client.getContrasenya());
        clientNou.setUsuari(client.getUsuari());
        clientNou.setReputacio(client.isReputacio());

        log.info("S'ha modificat el client.");

        String contrasenyaEncriptada = passwordEncoder.encode(client.getContrasenya());
        clientNou.setContrasenya(contrasenyaEncriptada);

        return clientRepository.save(clientNou);

    }

    public void baixaClient(Client client) {

        log.info("S'ha entrat al mètode baixaClient");

        Optional<Client> clientExistent = clientRepository.findByDni(client.getDni());

        if (clientExistent.isEmpty()) {
            throw new RuntimeException("No hi ha cap client amb aquest DNI");
        }

        clientRepository.delete(clientExistent.get());
        log.info("S'ha esborrat el client.");

    }

    public List<Client> llistarClients() {
        return clientRepository.findAll();
    }

    public Client obtenirClientPerDni(String dni) {
        return clientRepository.findById(dni).orElse(null);
    }

    public List<Client> cercarClients(String cognoms, String nacionalitat, String telefon, String email) {
        return clientRepository.cercarClients(
                (cognoms != null && !cognoms.isEmpty()) ? cognoms : null,
                (nacionalitat != null && !nacionalitat.isEmpty()) ? nacionalitat : null,
                (telefon != null && !telefon.isEmpty()) ? telefon : null,
                (email != null && !email.isEmpty()) ? email : null);
    }

    public Optional<Client> findByUsuari(String usuari) {
        return clientRepository.findByUsuari(usuari);
    }
}
