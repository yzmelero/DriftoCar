/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MongoDB;

import Projecte2.DriftoCar.entity.MongoDB.DocumentacioIncidencia;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositori per gestionar les operacions CRUD de la col·lecció
 * DocumentacioIncidencia` a MongoDB.
 * Extén {@link MongoRepository} per proporcionar funcionalitats predefinides.
 */
@Repository
public interface DocumentacioIncidenciaRepository extends MongoRepository<DocumentacioIncidencia, String> {

}