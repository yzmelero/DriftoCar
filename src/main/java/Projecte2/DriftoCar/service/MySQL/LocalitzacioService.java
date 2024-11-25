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
}
