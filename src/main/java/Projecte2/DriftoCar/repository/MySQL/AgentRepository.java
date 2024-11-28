/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MySQL;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import Projecte2.DriftoCar.entity.MySQL.Agent;

/**
 *
 * @author Anna
 */
@Repository
public interface AgentRepository extends JpaRepository<Agent, String> {
    
}
