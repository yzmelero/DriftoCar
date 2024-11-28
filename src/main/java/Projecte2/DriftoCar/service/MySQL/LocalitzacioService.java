/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MySQL;

import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.repository.MySQL.LocalitzacioRepository;
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

}
