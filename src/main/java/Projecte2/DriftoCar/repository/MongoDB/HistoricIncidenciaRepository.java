/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MongoDB;

import Projecte2.DriftoCar.entity.MongoDB.HistoricIncidencies;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


/**
 * Interface que hereda de MongoRepository para poder hacer uso de los métodos de MongoRepository
 * y poder hacer consultas a la base de datos de MongoDB. 
 * @author Anna
 */
@Repository
public interface HistoricIncidenciaRepository extends MongoRepository <HistoricIncidencies, String>{
    List<HistoricIncidencies> findByMatriculaContaining(String matricula);
}
