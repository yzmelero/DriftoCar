/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import java.util.List;
import java.util.Optional;

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
    
    @Autowired
    LocalitzacioRepository localitzacioRepository;

    public Agent altaAgent(Agent agent){
        Optional<Localitzacio> localitzacio = localitzacioRepository.findById(
            agent.getLocalitzacio().getCodiPostal());
        
        if (localitzacio.isEmpty()) {
            throw new RuntimeException("La localitzacio no existeix");
        }
        return agentRepository.save(agent);
    }
    public List<Agent> llistarAgents(){
        return agentRepository.findAll();
    }
}
