/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import Projecte2.DriftoCar.entity.MySQL.Agent;
import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.repository.MySQL.AgentRepository;
import Projecte2.DriftoCar.repository.MySQL.ClientRepository;
import Projecte2.DriftoCar.repository.MySQL.LocalitzacioRepository;

/**
 * Servei per gestionar les operacions relacionades amb els agents.
 * Aquesta classe permet la creació, modificació, eliminació i llistat d'agents,
 * així com la gestió de les localitzacions assignades als agents.
 */
@Service
public class AgentService {
    @Autowired
    AgentRepository agentRepository;

    Logger log = LoggerFactory.getLogger(AgentService.class);

    @Autowired
    LocalitzacioRepository localitzacioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ClientRepository clientRepository;

    /**
     * Crea un nou agent.
     *
     * @param agent L'agent a crear.
     * @return L'agent creat.
     */
    public Agent altaAgent(Agent agent) {
        log.info("S'ha entrat al mètode altaAgent.");

        // Verifica si ya existe un agente con el mismo DNI
        if (clientRepository.existsById(agent.getDni())) {
            log.warn("Ja existeix un agent amb aquest DNI.");
            throw new RuntimeException("Ja existeix un agent amb aquest DNI.");
        }

        Optional<Client> telefonExistent = clientRepository.findByTelefon(agent.getTelefon());

        if (telefonExistent.isPresent()) {
            log.warn("Aquest telefon ya esta asignat a un altre agent.");
            throw new RuntimeException("Aquest telefon ya esta asignat a un altre agent");
        }

        Optional<Client> agentExistent = clientRepository.findByUsuari(agent.getUsuari());

        if (agentExistent.isPresent()) {
            log.warn("Aquest nom d'usuari ja esta en us.");
            throw new RuntimeException("Aquest nom d'usuari ja esta en us.");
        }

        agentExistent = clientRepository.findByEmail(agent.getEmail());

        if (agentExistent.isPresent()) {
            log.warn("Aquest nom d'usuari ja esta en us.");
            throw new RuntimeException("Aquest email ja esta en us.");
        }

        agentExistent = clientRepository.findByNumTarjetaCredit(agent.getNumTarjetaCredit());

        if (agentExistent.isPresent()) {
            log.warn("Aquesta tarjeta de credit ja esta en us.");
            throw new RuntimeException("Aquesta tarjeta de credit ja esta en us.");
        }

        // Verifica si la localización ya tiene un agente asignado
        if (agent.getLocalitzacio() != null
                && localitzacioRepository.existsById(agent.getLocalitzacio().getCodiPostal())) {
            if (agentRepository.existsByLocalitzacio(agent.getLocalitzacio())) {
                log.warn("La localització ja està assignada a un altre agent.");
                throw new RuntimeException("La localització ja està assignada a un altre agent.");
            }
        }
        String contrasenyaEncriptada = passwordEncoder.encode(agent.getContrasenya());
        agent.setContrasenya(contrasenyaEncriptada);
        agent.setActivo(true);
        log.info("S'ha creat un agent.");

        // Guarda el nuevo agente
        return agentRepository.save(agent);

    }

    /**
     * Retorna tots els agents.
     *
     * @return Una llista de tots els agents.
     */
    public List<Agent> llistarAgents() {
        log.info("S'ha entrat al mètode llistarAgents del Service.");
        return agentRepository.findAll();
    }

    /**
     * Filtra els agents que contenen un DNI parcial o complet.
     *
     * @param dni El DNI per cercar.
     * @return Una llista d'agents que coincideixen amb el DNI.
     */
    public List<Agent> filtrarPerDni(String dni) {
        log.info("S'ha entrat al mètode filtrarPerDni del Service.");
        return agentRepository.findByDniContaining(dni);
    }

    /**
     * Modifica les dades d'un agent existent. Abans de modificar l'agent, es
     * realitzen
     * diverses comprovacions per garantir que els valors són vàlids i únics.
     * 
     * @param agent L'objecte Agent amb les dades actualitzades.
     * @return L'agent modificat.
     * @throws RuntimeException Si no existeix l'agent o si hi ha conflictes amb
     *                          dades úniques (usuari, email, etc.).
     */
    public Agent modificarAgent(Agent agent) {

        log.info("S'ha entrat al mètode modificarAgent del servei.");

        Optional<Client> agentExistent = clientRepository.findById(agent.getDni());

        if (agentExistent.isEmpty()) {
            log.error("No existeix cap client amb aquest DNI: {}", agent.getDni());
            throw new RuntimeException("No existeix cap client amb aquest DNI.");
        }
        agentExistent = clientRepository.findByUsuari(agent.getUsuari());
        log.debug("Comprovant que el nom d'usuari, email i número de targeta de crèdit siguin únics.");

        if (agentExistent.isPresent() && !agentExistent.get().getDni().equals(agent.getDni())) {
            log.error("Aquest nom d'usuari ja està en ús: {}", agent.getUsuari());
            throw new RuntimeException("Aquest nom d'usuari ja esta en us.");
        }
        agentExistent = clientRepository.findByEmail(agent.getEmail());
        if (agentExistent.isPresent() && !agentExistent.get().getDni().equals(agent.getDni())) {
            log.error("Aquest email ja està en ús: {}", agent.getEmail());
            throw new RuntimeException("Aquest email ja esta en us.");
        }
        agentExistent = clientRepository.findByNumTarjetaCredit(agent.getNumTarjetaCredit());
        if (agentExistent.isPresent() && !agentExistent.get().getDni().equals(agent.getDni())) {
            log.error("Aquesta targeta de crèdit ja està en ús: {}", agent.getNumTarjetaCredit());
            throw new RuntimeException("Aquesta tarjeta de credit ja esta en us.");
        }
        // Amb aquesta línia recuperem el client que ja existeix per a poder-lo
        // modificar.

        Optional<Agent> telefonExistent = agentRepository.findByTelefon(agent.getTelefon());

        if (telefonExistent.isPresent() && telefonExistent.get().getDni() != agent.getDni()) {
            log.error("Aquest telèfon ja està assignat a un altre agent: {}", agent.getTelefon());
            throw new RuntimeException("Aquest telefon ya esta asignat a un altre agent");
        }

        Optional<Agent> agentValidat = agentRepository.findById(agent.getDni());
        Agent agentNou = agentValidat.get();

        agentNou.setNom(agent.getNom());
        agentNou.setCognoms(agent.getCognoms());
        agentNou.setLlicencia(agent.getLlicencia());
        agentNou.setLlicCaducitat(agent.getLlicCaducitat());
        agentNou.setDniCaducitat(agent.getDniCaducitat());
        agentNou.setNumTarjetaCredit(agent.getNumTarjetaCredit());
        agentNou.setAdreca(agent.getAdreca());
        agentNou.setEmail(agent.getEmail());
        agentNou.setNacionalitat(agent.getNacionalitat());
        agentNou.setContrasenya(agent.getContrasenya());
        agentNou.setUsuari(agent.getUsuari());
        agentNou.setReputacio(agent.isReputacio());
        agentNou.setRol(agent.getRol());

        log.info("S'ha modificat l'agent.");

        agentNou.setContrasenya(agent.getContrasenya());

        log.info("S'ha encriptat la contrasenya");
        return agentRepository.save(agentNou);

    }

    /**
     * Recupera un agent per DNI.
     * 
     * @param dni El DNI de l'agent a recuperar.
     * @return L'agent amb el DNI indicat, o null si no es troba.
     */
    public Agent obtenirAgentPerDni(String dni) {
        log.debug("Recuperant l'agent amb DNI: {}", dni);
        return agentRepository.findById(dni).orElse(null);
    }

    /**
     * Elimina un agent de la base de dades.
     * 
     * @param agent L'objecte Agent a eliminar.
     * @throws RuntimeException Si l'agent no existeix.
     */
    public void eliminarAgent(Agent agent) {
        log.info("S'ha entrat al mètode eliminarAgent del servei.");

        if (!agentRepository.existsById(agent.getDni())) {
            log.error("L'agent amb DNI {} no existeix.", agent.getDni());
            throw new RuntimeException("L'agent amb DNI " + agent.getDni() + " no existeix.");
        }
        agentRepository.deleteById(agent.getDni()); // Elimina el agente si existe
        log.info("S'ha esborrat un agent.");

    }

    /**
     * Busca agents per DNI, utilitzant una cerca parcial.
     * 
     * @param dni El DNI o part del DNI a cercar.
     * @return Llista d'agents que coincideixen amb el criteri de cerca.
     */
    public List<Agent> buscarPorDni(String dni) {
        log.debug("Cercant agents amb el DNI que contingui: {}", dni);
        return agentRepository.findByDniContaining(dni); // Delega la búsqueda al repositorio
    }

    /**
     * Recupera les localitzacions disponibles per assignar a agents.
     * 
     * @return Llista de localitzacions no assignades a cap agent.
     */
    public List<Localitzacio> getLocalitzacionsDisponibles() {
        log.debug("Recuperant les localitzacions disponibles.");
        // Devuelve las localizaciones que no tienen asignado un agente
        return localitzacioRepository.findByAgentIsNull();
    }
}
