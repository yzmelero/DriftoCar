/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MySQL.LocalitzacioRepository;
import Projecte2.DriftoCar.repository.MySQL.VehicleRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Anna
 */
@Service
public class LocalitzacioService {

    @Autowired
    private LocalitzacioRepository localitzacioRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    public Localitzacio altaLocalitzacio(Localitzacio localitzacio) {

        if (localitzacioRepository.existsById(localitzacio.getCodiPostal())) {
            throw new RuntimeException("Ja existeix una localitzacio amb el codi postal: " + localitzacio.getCodiPostal());
        }
        return localitzacioRepository.save(localitzacio);
    }

    public void baixaLocalitzacio(String codiPostal) {
        if (!localitzacioRepository.existsById(codiPostal)) {
            throw new RuntimeException("No s'ha trobat cap localitzacio amb el codi postal: " + codiPostal);
        }

        Localitzacio localitzacio = localitzacioRepository.findById(codiPostal).get();

        List<Vehicle> vehicles = vehicleRepository.findByLocalitzacio_CodiPostal(codiPostal);

        if (!vehicles.isEmpty()) {
            throw new RuntimeException("No s'ha pogut eliminar la localitzacio perque el codi postal " +
                    codiPostal + " té vehicles associats!");
        }

        localitzacioRepository.deleteById(codiPostal);
    }
    
    public Localitzacio modificarLocalitzacio(String codiPostal, Localitzacio novaLocalitzacio) {
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
