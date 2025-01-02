/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MongoDB;

import Projecte2.DriftoCar.entity.MongoDB.DocumentacioIncidencia;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Repositori per gestionar les operacions CRUD de la col·lecció
 * `DocumentacioIncidencia` a MongoDB.
 * Extén {@link MongoRepository} per proporcionar funcionalitats predefinides.
 */
@Repository
public interface DocumentacioIncidenciaRepository extends MongoRepository<DocumentacioIncidencia, String> {
    /**
     * Cerca una llista de documentacions d'incidència associades a un identificador
     * d'incidència específic.
     *
     * @param incidenciaId l'identificador de l'incidència.
     * @return una llista de documentacions d'incidència amb l'identificador
     *         especificat.
     */
    List<DocumentacioIncidencia> findByIncidenciaId(Long incidenciaId);
}