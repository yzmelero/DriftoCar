/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MongoDB;

import Projecte2.DriftoCar.entity.MongoDB.HistoricIncidencies;
import Projecte2.DriftoCar.entity.MySQL.Incidencia;
import Projecte2.DriftoCar.repository.MongoDB.HistoricIncidenciaRepository;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author Anna
 */
@Service
public class HistoricIncidenciesService {

    @Autowired
    private HistoricIncidenciaRepository historicIncidenciesRepository;

    // Mètode per guardar la incidència a l'historial
    public void guardarHistoricIncidencia(Incidencia incidencia) {
        // Crear una nueva instancia de HistoricIncidencies y llenar los campos
        HistoricIncidencies historicIncidencia = new HistoricIncidencies();

        historicIncidencia.setId(String.valueOf(incidencia.getId()));

        historicIncidencia.setEstat(true);  // Estado de la incidencia (abierta/cerrada)
        historicIncidencia.setMotiu(incidencia.getMotiu());  // Motivo de la incidencia
        historicIncidencia.setDataIniciIncidencia(incidencia.getDataIniciIncidencia());  // Fecha de inicio de la incidencia
        historicIncidencia.setMatricula(incidencia.getMatricula().getMatricula());  // Matrícula del vehículo (relacionado con la entidad Vehicle)

        historicIncidenciesRepository.save(historicIncidencia);
    }
    
    // Método para guardar la incidencia tancada en el historial
    public void guardarHistoricIncidenciaTancada(Incidencia incidencia) {
        HistoricIncidencies historicIncidencia = new HistoricIncidencies();

        // Duplicar la información de la incidencia original, pero con estado tancado
        historicIncidencia.setId(String.valueOf(incidencia.getId()));  // ID de la incidencia
        historicIncidencia.setEstat(false);  // Estado tancado (falso)
        historicIncidencia.setMotiu(incidencia.getMotiu());
        historicIncidencia.setDataIniciIncidencia(incidencia.getDataIniciIncidencia());
        historicIncidencia.setMatricula(incidencia.getMatricula().getMatricula());

        // Guardar la incidencia tancada al historial de MongoDB (como un nuevo documento)
        historicIncidenciesRepository.save(historicIncidencia);
    }

    // Mètode per obtenir totes les incidències de l'historial
    public List<HistoricIncidencies> obtenirHistoric() {
        return historicIncidenciesRepository.findAll();
    }

    // Mètode per obtenir una incidència històrica per ID
    public HistoricIncidencies obtenirHistoricoPerId(String id) {
        return historicIncidenciesRepository.findById(id).orElse(null);
    }
}
