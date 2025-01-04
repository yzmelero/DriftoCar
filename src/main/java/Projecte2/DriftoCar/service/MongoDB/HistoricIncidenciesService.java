/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MongoDB;

import Projecte2.DriftoCar.entity.MongoDB.HistoricIncidencies;
import Projecte2.DriftoCar.entity.MySQL.Incidencia;
import Projecte2.DriftoCar.repository.MongoDB.HistoricIncidenciaRepository;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Servei per gestionar l'historial d'incidències.
 *
 * Aquesta classe permet guardar, consultar i gestionar les incidències
 * històriques
 * associades a vehicles, incloent-hi les seves dates i estat.
 *
 * Dependències principals:
 * {@link HistoricIncidenciaRepository} - Repositori per accedir a les dades de
 * l'historial.
 *
 */
@Service
public class HistoricIncidenciesService {

    private static final Logger logger = LoggerFactory.getLogger(HistoricIncidenciesService.class);

    @Autowired
    private HistoricIncidenciaRepository historicIncidenciesRepository;

    /**
     * Guarda una incidència oberta a l'historial.
     *
     * @param incidencia la incidència a guardar.
     */
    public void guardarHistoricIncidencia(Incidencia incidencia) {
        logger.info("Guardant una incidència oberta a l'historial.");

        // Crear una nueva instancia de HistoricIncidencies y llenar los campos
        HistoricIncidencies historicIncidencia = new HistoricIncidencies();

        historicIncidencia.setId(String.valueOf(incidencia.getId()));

        historicIncidencia.setEstat(true); // Estado de la incidencia (abierta/cerrada)
        historicIncidencia.setMotiu(incidencia.getMotiu()); // Motivo de la incidencia
        historicIncidencia.setDataIniciIncidencia(incidencia.getDataIniciIncidencia()); // Fecha de inicio de la
                                                                                        // incidencia
        historicIncidencia.setMatricula(incidencia.getMatricula().getMatricula()); // Matrícula del vehículo
                                                                                   // (relacionado con la entidad
                                                                                   // Vehicle)

        historicIncidenciesRepository.save(historicIncidencia);
        logger.debug("Incidència guardada correctament amb ID: {}", incidencia.getId());
    }

    /**
     * Guarda una incidència tancada a l'historial amb una nova ID.
     *
     * @param incidencia la incidència a guardar com a tancada.
     */
    public void guardarHistoricIncidenciaTancada(Incidencia incidencia) {
        logger.info("Guardant una incidència tancada a l'historial.");
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
        logger.debug("Incidència tancada guardada correctament amb nova ID: {}", maximaId + 1);
    }

    /**
     * Obté totes les incidències de l'historial.
     *
     * @return llista d'incidències històriques.
     */
    public List<HistoricIncidencies> obtenirHistoric() {
        logger.info("Obtenint totes les incidències de l'historial.");
        return historicIncidenciesRepository.findAll();
    }

    /**
     * Obté una incidència històrica per ID.
     *
     * @param id l'ID de la incidència a consultar.
     * @return l'incidència històrica associada a l'ID, o null si no es troba.
     */
    public HistoricIncidencies obtenirHistoricoPerId(String id) {
        logger.info("Obtenint la incidència històrica amb ID: {}", id);
        return historicIncidenciesRepository.findById(id).orElse(null);
    }

    /**
     * Obté una llista d'incidències històriques filtrades per matrícula.
     *
     * @param matricula la matrícula pel qual filtrar les incidències (pot ser
     *                  parcial).
     * @return llista d'incidències que coincideixen amb la matrícula indicada.
     */
    public List<HistoricIncidencies> findByMatricula(String matricula) {
        logger.info("Buscant incidències històriques per matrícula: {}", matricula);
        if (matricula == null || matricula.isBlank()) {
            logger.debug("Matrícula no proporcionada o buida. Retornant totes les incidències.");
            return historicIncidenciesRepository.findAll(); // Devuelve todo el listado si no se filtra
        }
        return historicIncidenciesRepository.findByMatriculaContaining(matricula);
    }
}
