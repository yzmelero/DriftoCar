/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MongoDB;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import Projecte2.DriftoCar.entity.MongoDB.HistoricReserves;
/**
 *
 * @author Anna
 */
@Repository
public interface HistoricReservesRepository extends MongoRepository<HistoricReserves, String> {

}
