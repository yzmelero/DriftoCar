/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MySQL.AgentRepository;
import Projecte2.DriftoCar.repository.MySQL.LocalitzacioRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;
import java.util.List;
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
public class LocalitzacioService {

    Logger log = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    private LocalitzacioRepository localitzacioRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private AgentRepository agentRepository;

    public Localitzacio altaLocalitzacio(Localitzacio localitzacio) {
        log.info("S'ha entrat al mètode altaLocalitzacio.");

        if (localitzacioRepository.existsById(localitzacio.getCodiPostal())) {
            throw new RuntimeException(
                    "Ja existeix una localitzacio amb el codi postal: " + localitzacio.getCodiPostal());
        }
        return localitzacioRepository.save(localitzacio);
    }

    public void baixaLocalitzacio(String codiPostal) {
        log.info("S'ha entrat al mètode baixaLocalització.");

        if (!localitzacioRepository.existsById(codiPostal)) {
            
        }
        Optional<Localitzacio> localitzacio = localitzacioRepository.findById(codiPostal);
        if (localitzacio.isEmpty()) {
            throw new RuntimeException("No s'ha trobat cap localitzacio amb el codi postal: " + codiPostal);
        }
        Localitzacio existeix = localitzacio.get();
        if (agentRepository.existsByLocalitzacio(existeix)) {
            throw new RuntimeException("No es pot eliminar la localització perquè està assignada a un agent.");

        }

        List<Vehicle> vehicles = vehicleRepository.findByLocalitzacio_CodiPostal(codiPostal);

        if (!vehicles.isEmpty()) {
            throw new RuntimeException("No s'ha pogut eliminar la localitzacio perque el codi postal " +
                    codiPostal + " té vehicles associats!");
        }

        localitzacioRepository.deleteById(codiPostal);
    }

    public Localitzacio modificarLocalitzacio(String codiPostal, Localitzacio novaLocalitzacio) {
        log.info("S'ha entrat al mètode modificarLocalitzacio.");

        if (!localitzacioRepository.existsById(codiPostal)) {
            throw new RuntimeException("No s'ha trobat cap localitzacio amb el codi postal: " + codiPostal);
        }

        Localitzacio localitzacioExistente = localitzacioRepository.findById(codiPostal).get();

        localitzacioExistente.setCiutat(novaLocalitzacio.getCiutat());
        localitzacioExistente.setAdrecaLocalitzacio(novaLocalitzacio.getAdrecaLocalitzacio());
        localitzacioExistente.setHorari(novaLocalitzacio.getHorari());
        localitzacioExistente.setCondicions(novaLocalitzacio.getCondicions());

        return localitzacioRepository.save(localitzacioExistente);
    }
    
    public List<Localitzacio> llistarLocalitzacions() {
        return localitzacioRepository.findAll();
    }
    
    public Localitzacio obtenirLocalitzacioCodiPostal(String codiPostal) {
        return localitzacioRepository.findById(codiPostal).orElse(null);
    }
    
}
