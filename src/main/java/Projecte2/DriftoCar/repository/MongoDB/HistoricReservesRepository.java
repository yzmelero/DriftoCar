/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.repository.MongoDB;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import Projecte2.DriftoCar.entity.MongoDB.HistoricReserves;
import java.util.List;


/**
 * Repositori per gestionar les operacions CRUD de la col·lecció `HistoricReserves` a MongoDB.
 * Extén {@link MongoRepository} per proporcionar funcionalitats predefinides.
 */
@Repository
public interface HistoricReservesRepository extends MongoRepository<HistoricReserves, String> {
    /**
     * Cerca una llista d'històrics de reserves segons un dni.
     *
     * @param dni el dni pertenyent a la reserva.
     * @return una llista de reserves filtrades per un dni.
     */
    List<HistoricReserves> findByDNIContaining(String dni);
}
