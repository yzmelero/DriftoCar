/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.repository.MySQL.ClientRepository;
import Projecte2.DriftoCar.repository.MySQL.ReservaRepository;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servei per gestionar les operacions relacionades amb els clients.
 * Aquesta classe permet la creació i modificació de clients, així com la
 * validació de dades abans de la creació del client.
 * També s'inclou la gestió de les reserves associades als clients.
 */
@Service
public class ClientService {

    @Autowired
    ClientRepository clientRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ReservaRepository reservaRepository;

    Logger log = LoggerFactory.getLogger(ClientService.class);

    /**
     * Constructor de la classe `ClientService` que inicialitza el repositori de
     * clients.
     * 
     * @param clientRepository El repositori de clients que s'utilitzarà per accedir
     *                         a la base de dades.
     */
    public ClientService(ClientRepository clientRepository) {

        this.clientRepository = clientRepository;

    }

    /**
     * Crea un nou client a la base de dades.
     * Abans de crear el client, es realitza una validació per garantir que les
     * dades siguin correctes.
     * 
     * @param client L'objecte Client amb les dades del client a crear.
     * @return El client creat.
     */
    public Client altaClient(Client client) throws Exception {
        log.info("S'ha entrat al mètode altaClient");

        if (client.getUsuari() == null || client.getUsuari().isEmpty()) {
            log.error("L'usuari no pot ser buit.");
            throw new IllegalArgumentException("El camp usuari no pot estar buit.");
        }
        if (client.getEmail() == null || client.getEmail().isEmpty()) {
            log.error("L'email no pot ser buit.");
            throw new IllegalArgumentException("El camp email no pot estar buit.");
        }
        if (client.getContrasenya() == null || client.getContrasenya().isEmpty()) {
            log.error("El camp contrasenya no pot estar buit.");
            throw new IllegalArgumentException("El camp contrasenya no pot estar buit.");
        }
        if (client.getDni() == null || client.getDni().isEmpty()) {
            log.error("El camp DNI no pot estar buit.");
            throw new IllegalArgumentException("El camp DNI no pot estar buit.");
        }
        if (client.getNom() == null || client.getNom().isEmpty()) {
            log.error("El camp nom no pot estar buit.");
            throw new IllegalArgumentException("El camp nom no pot estar buit.");
        }
        if (client.getCognoms() == null || client.getCognoms().isEmpty()) {
            log.error("El camp cognoms no pot estar buit.");
            throw new IllegalArgumentException("El camp cognoms no pot estar buit.");
        }
        if (client.getNacionalitat() == null || client.getNacionalitat().isEmpty()) {
            log.error("El camp nacionalitat no pot estar buit.");
            throw new IllegalArgumentException("El camp nacionalitat no pot estar buit.");
        }
        if (client.getTelefon() == null || client.getTelefon().isEmpty()) {
            log.error("El camp telèfon no pot estar buit.");
            throw new IllegalArgumentException("El camp telèfon no pot estar buit.");
        }
        if (client.getLlicencia() == null || client.getLlicencia().isEmpty()) {
            log.error("El camp llicència no pot estar buit.");
            throw new IllegalArgumentException("El camp llicència no pot estar buit.");
        }
        if (client.getLlicCaducitat() == null || client.getLlicCaducitat().toString().isEmpty()) {
            log.error("El camp caducitat de la llicència no pot estar buit.");
            throw new IllegalArgumentException("El camp caducitat de la llicència no pot estar buit.");
        }
        if (client.getDniCaducitat() == null || client.getDniCaducitat().toString().isEmpty()) {
            log.error("El camp caducitat del dni no pot estar buit.");
            throw new IllegalArgumentException("El camp caducitat del dni no pot estar buit.");
        }
        if (client.getNumTarjetaCredit() == null || client.getNumTarjetaCredit().toString().isEmpty()) {
            log.error("El camp tarjeta de crèdit no pot estar buit.");
            throw new IllegalArgumentException("El camp tarjeta de crèdit no pot estar buit.");
        }
        if (client.getAdreca() == null || client.getAdreca().isEmpty()) {
            log.error("El camp adreça no pot estar buit.");
            throw new IllegalArgumentException("El camp adreça no pot estar buit.");
        }

        // Comprovar si ja existeix un client amb el dni inserit.
        Optional<Client> clientExistent = clientRepository.findByDni(client.getDni());

        if (clientExistent.isPresent()) {
            log.error("Ja existeix un client amb aquest DNI.");
            throw new Exception("Ja existeix un client amb aquest DNI.");
        }

        Optional<Client> usuariExistent = clientRepository.findByUsuari(client.getUsuari());

        if (usuariExistent.isPresent()) {
            log.error("Aquest nom d'usuari ja esta en us.");
            throw new RuntimeException("Aquest nom d'usuari ja esta en us.");
        }

        Optional<Client> emailExistent = clientRepository.findByEmail(client.getEmail());

        if (emailExistent.isPresent()) {
            log.error("Aquest email ja està en ús.");
            throw new RuntimeException("Aquest email ja esta en us.");
        }

        Optional<Client> numTarjetaCreditExistent = clientRepository
                .findByNumTarjetaCredit(client.getNumTarjetaCredit());

        if (numTarjetaCreditExistent.isPresent()) {
            log.error("Aquesta tarjeta de crèdit ja està en ús.");
            throw new RuntimeException("Aquesta tarjeta de credit ja esta en us.");
        }

        Optional<Client> telefonExistent = clientRepository.findByTelefon(client.getTelefon());

        if (telefonExistent.isPresent()) {
            log.error("Aquest telèfon ja està en ús.");
            throw new RuntimeException("Aquest telefon ya esta asignat a un altre client");
        }

        log.info("S'ha donat d'alta a un client.");

        String contrasenyaEncriptada = passwordEncoder.encode(client.getContrasenya());
        client.setContrasenya(contrasenyaEncriptada);

        log.info("S'ha encriptat la contrasenya");

        return clientRepository.save(client);

    }

    /**
     * Modifica les dades d'un client existent. Abans de modificar el client, es
     * realitzen
     * diverses comprovacions per garantir que els valors siguin vàlids i únics.
     * 
     * @param client L'objecte Client amb les dades actualitzades.
     * @return El client modificat.
     * @throws RuntimeException Si no existeix el client o si hi ha conflictes amb
     *                          dades úniques (usuari, email, etc.).
     */
    public Client modificarClient(Client client) {

        log.info("S'ha entrat al mètode modificarClient");

        Optional<Client> clientExistent = clientRepository.findByDni(client.getDni());

        if (clientExistent.isEmpty()) {
            log.error("No existeix cap client amb aquest DNI: {}", client.getDni());
            throw new RuntimeException("No existeix cap client amb aquest DNI.");
        }

        // Amb aquesta línia recuperem el client que ja existeix per a poder-lo
        // modificar.
        Client clientNou = clientExistent.get();

        // Loggers per a corregir un bug.
        log.debug("Client rebut: {}", client);
        log.debug("Telèfon rebut: {}", client.getTelefon());
        log.debug("Nacionalitat rebuda: {}", client.getNacionalitat());

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
        clientNou.setContrasenya(client.getContrasenya());
        Optional<Client> telefonExistent = clientRepository.findByTelefon(client.getTelefon());

        if (telefonExistent.isPresent() && telefonExistent.get().getDni() != client.getDni()) {
            log.error("Aquest telèfon ja està assignat a un altre client: {}", client.getTelefon());
            throw new RuntimeException("Aquest telefon ya esta asignat a un altre client");
        }

        Optional<Client> usuariExistent = clientRepository.findByUsuari(client.getUsuari());

        if (usuariExistent.isPresent() && !usuariExistent.get().getDni().equals(client.getDni())) {
            log.error("Aquest nom d'usuari ja està en ús: {}", client.getUsuari());
            throw new RuntimeException("Aquest nom d'usuari ja esta en us.");
        }

        Optional<Client> emailExistent = clientRepository.findByEmail(client.getEmail());

        if (emailExistent.isPresent() && !emailExistent.get().getDni().equals(client.getDni())) {
            log.error("Aquest email ja està en ús: {}", client.getEmail());
            throw new RuntimeException("Aquest email ja esta en us.");
        }

        Optional<Client> numTarjetaCreditExistent = clientRepository
                .findByNumTarjetaCredit(client.getNumTarjetaCredit());

        if (numTarjetaCreditExistent.isPresent() && !numTarjetaCreditExistent.get().getDni().equals(client.getDni())) {
            log.error("Aquesta targeta de crèdit ja està en ús: {}", client.getNumTarjetaCredit());
            throw new RuntimeException("Aquesta tarjeta de credit ja esta en us.");
        }

        log.info("S'ha modificat el client amb DNI: {}", client.getDni());

        return clientRepository.save(clientNou);

    }

    /**
     * Elimina un client de la base de dades. Si el client té reserves associades,
     * no es pot eliminar.
     * 
     * @param client L'objecte Client a eliminar.
     * @throws RuntimeException Si el client té reserves associades o no existeix.
     */
    public void baixaClient(Client client) {

        log.info("S'ha entrat al mètode baixaClient");

        Optional<Client> clientExistent = clientRepository.findByDni(client.getDni());

        // si el client te reservas asociades no pot ser eliminat
        if (reservaRepository.existsByClientDni(client.getDni())) {
            log.error("No es pot esborrar el client amb DNI {} perquè té reserves associades.", client.getDni());
            throw new RuntimeException("No es pot esborrar el client perquè té reserves associades.");
        }

        if (clientExistent.isEmpty()) {
            log.error("No hi ha cap client amb aquest DNI: {}", client.getDni());
            throw new RuntimeException("No hi ha cap client amb aquest DNI");
        }

        clientRepository.delete(clientExistent.get());
        log.info("S'ha esborrat el client amb DNI: {}", client.getDni());
    }

    /**
     * Llista tots els clients de la base de dades.
     * 
     * @return Llista de clients.
     */
    public List<Client> llistarClients() {
        log.debug("Recuperant la llista de clients.");
        return clientRepository.findAll();
    }

    /**
     * Recupera un client per DNI.
     * 
     * @param dni El DNI del client a recuperar.
     * @return El client amb el DNI indicat, o null si no es troba.
     */
    public Client obtenirClientPerDni(String dni) {
        log.debug("Recuperant el client amb DNI: {}", dni);
        return clientRepository.findById(dni).orElse(null);
    }

    /**
     * Cerca clients en funció dels criteris proporcionats.
     * 
     * @param cognoms      Cognoms del client a cercar.
     * @param nacionalitat Nacionalitat del client a cercar.
     * @param telefon      Telèfon del client a cercar.
     * @param email        Email del client a cercar.
     * @return Llista de clients que coincideixen amb els criteris de cerca.
     */
    public List<Client> cercarClients(String cognoms, String nacionalitat, String telefon, String email) {
        log.debug("Cercant clients amb els criteris: cognoms={}, nacionalitat={}, telefon={}, email={}",
                cognoms, nacionalitat, telefon, email);

        return clientRepository.cercarClients(
                (cognoms != null && !cognoms.isEmpty()) ? cognoms : null,
                (nacionalitat != null && !nacionalitat.isEmpty()) ? nacionalitat : null,
                (telefon != null && !telefon.isEmpty()) ? telefon : null,
                (email != null && !email.isEmpty()) ? email : null);
    }

    /**
     * Recupera un client pel nom d'usuari.
     * 
     * @param usuari El nom d'usuari del client a buscar.
     * @return Un objecte Optional amb el client trobat o buit si no existeix.
     */
    public Optional<Client> findByUsuari(String usuari) {
        log.debug("Cercant el client amb nom d'usuari: {}", usuari);
        return clientRepository.findByUsuari(usuari);
    }

    /**
     * Llista els clients inactius (aquells que tenen "activo" a false).
     * 
     * @return Llista de clients inactius.
     */
    public List<Client> listarClientsInactius() {
        log.debug("Recuperant els clients inactius.");
        return clientRepository.findByActivoFalse();
    }

    /**
     * Activa un client, establint el camp "activo" a true.
     * 
     * @param dni El DNI del client a activar.
     * @throws RuntimeException Si el client no es troba.
     */
    public void activarClient(String dni) {
        log.debug("Activant el client amb DNI: {}", dni);

        Client client = clientRepository.findById(dni)
                .orElseThrow(() -> new RuntimeException("Client no trobat"));

        client.setActivo(true); // Activa el usuario
        log.info("S'ha activat el client amb DNI: {}", dni);
        clientRepository.save(client);
    }

    /**
     * Recupera un client pel seu DNI.
     * 
     * @param dni El DNI del client a buscar.
     * @return Un objecte Optional amb el client trobat o buit si no existeix.
     */
    public Optional<Client> findByDni(String dni) {
        log.debug("Cercant el client amb DNI: {}", dni);
        return clientRepository.findByDni(dni);
    }
}