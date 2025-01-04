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
 * Servei per gestionar les localitzacions dels vehicles, incloent l'alta, baixa
 * i modificació de les localitzacions.
 * També permet obtenir llistats de localitzacions i consultar una localització
 * per codi postal.
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

    /**
     * Alta d'una nova localització al sistema.
     * Si ja existeix una localització amb el mateix codi postal, es llença una
     * excepció.
     * 
     * @param localitzacio La localització a alta.
     * @return La localització alta.
     */
    public Localitzacio altaLocalitzacio(Localitzacio localitzacio) {
        log.info("S'ha entrat al mètode altaLocalitzacio del servei.");

        if (localitzacioRepository.existsById(localitzacio.getCodiPostal())) {
            log.error("Ja existeix una localitzacio amb el codi postal: {}", localitzacio.getCodiPostal());
            throw new RuntimeException(
                    "Ja existeix una localitzacio amb el codi postal: " + localitzacio.getCodiPostal());
        }
        log.info("Localització amb codi postal {} alta correctament.", localitzacio.getCodiPostal());
        return localitzacioRepository.save(localitzacio);
    }

    /**
     * Baixa d'una localització per codi postal.
     * Si la localització està associada a un agent o té vehicles associats, es
     * llença una excepció.
     * 
     * @param codiPostal El codi postal de la localització a eliminar.
     */
    public void baixaLocalitzacio(String codiPostal) {
        log.info("S'ha entrat al mètode baixaLocalització del servei.");

        if (!localitzacioRepository.existsById(codiPostal)) {
            log.error("No s'ha trobat cap localitzacio amb el codi postal: {}", codiPostal);

        }
        Optional<Localitzacio> localitzacio = localitzacioRepository.findById(codiPostal);
        if (localitzacio.isEmpty()) {
            log.error("No s'ha trobat cap localitzacio amb el codi postal: {}", codiPostal);
            throw new RuntimeException("No s'ha trobat cap localitzacio amb el codi postal: " + codiPostal);
        }
        Localitzacio existeix = localitzacio.get();
        if (agentRepository.existsByLocalitzacio(existeix)) {
            log.error("No es pot eliminar la localització perquè està assignada a un agent: {}", codiPostal);
            throw new RuntimeException("No es pot eliminar la localització perquè està assignada a un agent.");

        }

        List<Vehicle> vehicles = vehicleRepository.findByLocalitzacio_CodiPostal(codiPostal);

        if (!vehicles.isEmpty()) {
            log.error("No s'ha pogut eliminar la localitzacio perque el codi postal {} té vehicles associats!",
                    codiPostal);
            throw new RuntimeException("No s'ha pogut eliminar la localitzacio perque el codi postal " +
                    codiPostal + " té vehicles associats!");
        }
        localitzacioRepository.deleteById(codiPostal);
        log.info("Localització amb codi postal {} eliminada correctament.", codiPostal);
    }

    /**
     * Modifica una localització existent amb el codi postal especificat.
     * Si no es troba la localització, es llença una excepció.
     * 
     * @param codiPostal       El codi postal de la localització a modificar.
     * @param novaLocalitzacio La nova localització amb les dades actualitzades.
     * @return La localització modificada.
     */
    public Localitzacio modificarLocalitzacio(String codiPostal, Localitzacio novaLocalitzacio) {
        log.info("S'ha entrat al mètode modificarLocalitzacio del servei.");

        if (!localitzacioRepository.existsById(codiPostal)) {
            log.error("No s'ha trobat cap localitzacio amb el codi postal: {}", codiPostal);
            throw new RuntimeException("No s'ha trobat cap localitzacio amb el codi postal: " + codiPostal);
        }

        Localitzacio localitzacioExistente = localitzacioRepository.findById(codiPostal).get();

        localitzacioExistente.setCiutat(novaLocalitzacio.getCiutat());
        localitzacioExistente.setAdrecaLocalitzacio(novaLocalitzacio.getAdrecaLocalitzacio());
        localitzacioExistente.setHorari(novaLocalitzacio.getHorari());
        localitzacioExistente.setCondicions(novaLocalitzacio.getCondicions());

        log.info("Localització amb codi postal {} modificada correctament.", codiPostal);
        return localitzacioRepository.save(localitzacioExistente);
    }

    /**
     * Llista totes les localitzacions registrades.
     * 
     * @return Una llista de totes les localitzacions.
     */
    public List<Localitzacio> llistarLocalitzacions() {
        log.info("S'ha entrat al mètode llistarLocalitzacions del servei.");
        return localitzacioRepository.findAll();
    }

    /**
     * Obté una localització per codi postal.
     * 
     * @param codiPostal El codi postal de la localització a obtenir.
     * @return La localització trobada o null si no es troba.
     */
    public Localitzacio obtenirLocalitzacioCodiPostal(String codiPostal) {
        log.info("S'ha entrat al mètode obtenirLocalitzacioCodiPostal del servei per codi postal: {}", codiPostal);
        return localitzacioRepository.findById(codiPostal).orElse(null);
    }

}
