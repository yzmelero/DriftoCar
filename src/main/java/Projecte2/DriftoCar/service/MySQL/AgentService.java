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
import org.springframework.stereotype.Service;

import Projecte2.DriftoCar.entity.MySQL.Agent;
import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.repository.MySQL.AgentRepository;
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

    public Agent altaAgent(Agent agent) {
        Optional<Localitzacio> localitzacio = localitzacioRepository.findById(
                agent.getLocalitzacio().getCodiPostal());

        if (localitzacio.isEmpty()) {
            throw new RuntimeException("La localitzacio no existeix");
        }
        Optional<Agent> agentExistent = agentRepository.findById(agent.getDni());

        if (agentExistent.isPresent()) {
            throw new RuntimeException("Ja existeix un agent amb aquest DNI.");
        }
        return agentRepository.save(agent);
    }

    public List<Agent> llistarAgents() {
        return agentRepository.findAll();
    }

    public Agent modificarAgent(Agent agent) {

        log.info("S'ha entrat al mètode modificarAgent");

        Optional<Agent> agentExistent = agentRepository.findById(agent.getDni());

        if (agentExistent.isEmpty()) {
            throw new RuntimeException("No existeix cap client amb aquest DNI.");
        }

        // Amb aquesta línia recuperem el client que ja existeix per a poder-lo
        // modificar.
        Agent agentAntic = agentExistent.get();

        agentAntic.setNom(agent.getNom());
        agentAntic.setCognoms(agent.getCognoms());
        agentAntic.setLlicencia(agent.getLlicencia());
        agentAntic.setLlicCaducitat(agent.getLlicCaducitat());
        agentAntic.setDniCaducitat(agent.getDniCaducitat());
        agentAntic.setNumTarjetaCredit(agent.getNumTarjetaCredit());
        agentAntic.setAdreca(agent.getAdreca());
        agentAntic.setEmail(agent.getEmail());
        agentAntic.setContrasenya(agent.getContrasenya());
        agentAntic.setUsuari(agent.getUsuari());
        agentAntic.setReputacio(agent.isReputacio());
        agentAntic.setRol(agent.getRol());

        log.info("S'ha modificat el client.");
        return agentRepository.save(agentAntic);

    }

    public Agent obtenirAgentPerDni(String dni) {
        return agentRepository.findById(dni).orElse(null);
    }
    
    public void eliminarAgent(Agent agent) {
        if (!agentRepository.existsById(agent.getDni())) {
            throw new RuntimeException("L'agent amb DNI " + agent.getDni() + " no existeix.");
        }
        agentRepository.deleteById(agent.getDni()); // Elimina el agente si existe
    }

    public List<Agent> buscarPorDni(String dni) {
        return agentRepository.findByDniContaining(dni); // Delega la búsqueda al repositorio
    }
    
}
