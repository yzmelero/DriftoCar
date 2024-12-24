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

    // Mètode per guardar la incidència tancada al historial amb una nova ID
    public void guardarHistoricIncidenciaTancada(Incidencia incidencia) {
        // Obtenir totes les incidències de l'historial
        List<HistoricIncidencies> totesLesIncidencies = obtenirHistoric();

        // Inicialitzar la màxima ID a 0
        long maximaId = 0;

        // Recórrer totes les incidències per trobar la màxima ID
        for (HistoricIncidencies incidenciaHist : totesLesIncidencies) {
            if (incidenciaHist.getId() != null) { // Comprovem que l'ID no sigui nul
                long idActual = Long.parseLong(incidenciaHist.getId());
                if (idActual > maximaId) {
                    maximaId = idActual;
                }
            }
        }

        // Calcular la nova ID com a màxima ID més 1
        long novaId = maximaId + 1;

        // Crear una nova instància de HistoricIncidencies
        HistoricIncidencies historicIncidencia = new HistoricIncidencies();

        // Assignar els valors a la nova incidència històrica
        historicIncidencia.setId(String.valueOf(novaId)); // Assignem la nova ID
        historicIncidencia.setEstat(false); // Estat tancat (fals)
        historicIncidencia.setMotiu(incidencia.getMotiu()); // Motiu de la incidència
        historicIncidencia.setDataIniciIncidencia(incidencia.getDataIniciIncidencia()); // Data d'inici
        historicIncidencia.setDataFiIncidencia(incidencia.getDataFiIncidencia()); // Data de tancament
        historicIncidencia.setMatricula(incidencia.getMatricula().getMatricula()); // Matrícula del vehicle

        // Guardar la nova incidència tancada a MongoDB
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
