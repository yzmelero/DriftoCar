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
 *
 * @author Anna
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
        // Verifica si ya existe un agente con el mismo DNI
        if (clientRepository.existsById(agent.getDni())) {
            throw new RuntimeException("Ja existeix un agent amb aquest DNI.");
        }

        Optional<Client> telefonExistent = clientRepository.findByTelefon(agent.getTelefon());

        if (telefonExistent.isPresent()) {
            throw new RuntimeException("Aquest telefon ya esta asignat a un altre agent");
        }

        Optional<Client> agentExistent = clientRepository.findByUsuari(agent.getUsuari());

        if (agentExistent.isPresent()) {
            throw new RuntimeException("Aquest nom d'usuari ja esta en us.");
        }

        agentExistent = clientRepository.findByEmail(agent.getEmail());

        if (agentExistent.isPresent()) {
            throw new RuntimeException("Aquest email ja esta en us.");
        }

        agentExistent = clientRepository.findByNumTarjetaCredit(agent.getNumTarjetaCredit());

        if (agentExistent.isPresent()) {
            throw new RuntimeException("Aquesta tarjeta de credit ja esta en us.");
        }


        // Verifica si la localización ya tiene un agente asignado
        if (agent.getLocalitzacio() != null
                && localitzacioRepository.existsById(agent.getLocalitzacio().getCodiPostal())) {
            if (agentRepository.existsByLocalitzacio(agent.getLocalitzacio())) {
                throw new RuntimeException("La localització ja està assignada a un altre agent.");
            }
        }
        String contrasenyaEncriptada = passwordEncoder.encode(agent.getContrasenya());
        agent.setContrasenya(contrasenyaEncriptada);
        agent.setActivo(true);
        // Guarda el nuevo agente
        return agentRepository.save(agent);

    }

    /**
     * Retorna tots els agents.
     *
     * @return Una llista de tots els agents.
     */
    public List<Agent> llistarAgents() {
        return agentRepository.findAll();
    }

    /**
     * Filtra els agents que contenen un DNI parcial o complet.
     *
     * @param dni El DNI per cercar.
     * @return Una llista d'agents que coincideixen amb el DNI.
     */
    public List<Agent> filtrarPerDni(String dni) {
        return agentRepository.findByDniContaining(dni);
    }

    public Agent modificarAgent(Agent agent) {

        log.info("S'ha entrat al mètode modificarAgent");

        Optional<Client> agentExistent = clientRepository.findById(agent.getDni());

        if (agentExistent.isEmpty()) {
            throw new RuntimeException("No existeix cap client amb aquest DNI.");
        }
        agentExistent = clientRepository.findByUsuari(agent.getUsuari());
        if (agentExistent.isPresent() && !agentExistent.get().getDni().equals(agent.getDni())) {
            throw new RuntimeException("Aquest nom d'usuari ja esta en us.");
        }
        agentExistent = clientRepository.findByEmail(agent.getEmail());
        if (agentExistent.isPresent() && !agentExistent.get().getDni().equals(agent.getDni())) {
            throw new RuntimeException("Aquest email ja esta en us.");
        }
        agentExistent = clientRepository.findByNumTarjetaCredit(agent.getNumTarjetaCredit());
        if (agentExistent.isPresent() && !agentExistent.get().getDni().equals(agent.getDni())) {
            throw new RuntimeException("Aquesta tarjeta de credit ja esta en us.");
        }
        // Amb aquesta línia recuperem el client que ja existeix per a poder-lo
        // modificar.

        Optional<Agent> telefonExistent = agentRepository.findByTelefon(agent.getTelefon());

        if (telefonExistent.isPresent() && telefonExistent.get().getDni() != agent.getDni()) {
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

    public Agent obtenirAgentPerDni(String dni) {
        return agentRepository.findById(dni).orElse(null);
    }

    public void eliminarAgent(Agent agent) {
        log.info("S'ha entrat al mètode eliminarAgent.");

        if (!agentRepository.existsById(agent.getDni())) {
            throw new RuntimeException("L'agent amb DNI " + agent.getDni() + " no existeix.");
        }
        agentRepository.deleteById(agent.getDni()); // Elimina el agente si existe
        log.info("S'ha esborrat un agent.");

    }

    public List<Agent> buscarPorDni(String dni) {
        return agentRepository.findByDniContaining(dni); // Delega la búsqueda al repositorio
    }

    public List<Localitzacio> getLocalitzacionsDisponibles() {
        // Devuelve las localizaciones que no tienen asignado un agente
        return localitzacioRepository.findByAgentIsNull();
    }
}
