/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MongoDB;

import Projecte2.DriftoCar.entity.MongoDB.HistoricIncidencies;
import Projecte2.DriftoCar.entity.MySQL.Incidencia;
import Projecte2.DriftoCar.repository.MongoDB.HistoricIncidenciaRepository;
import Projecte2.DriftoCar.service.MySQL.IncidenciaService;
import java.time.LocalDateTime;
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

    @Autowired
    private IncidenciaService incidenciaService;

    // Mètode per guardar la incidència a l'historial
    public void guardarHistoricIncidencia(Incidencia incidencia) {
        // Crear una nueva instancia de HistoricIncidencies y llenar los campos
        HistoricIncidencies historicIncidencia = new HistoricIncidencies();

        historicIncidencia.setEstat(incidencia.isEstat());  // Estado de la incidencia (abierta/cerrada)
        historicIncidencia.setMotiu(incidencia.getMotiu());  // Motivo de la incidencia
        historicIncidencia.setDataIniciIncidencia(incidencia.getDataIniciIncidencia());  // Fecha de inicio de la incidencia
        historicIncidencia.setMatricula(incidencia.getMatricula().getMatricula());  // Matrícula del vehículo (relacionado con la entidad Vehicle)

        // Guardar la incidencia en MongoDB
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

    // Mètodes per obtenir les fotos i PDFs en Base64
    private String[] obtenirFotosBase64(Long incidenciaId) {
        // Lògica per obtenir fotos en Base64 (pot ser des d'un sistema de fitxers o base de dades)
        return new String[]{}; // Torna les fotos en Base64
    }

    private String[] obtenirPdfBase64(Long incidenciaId) {
        // Lògica per obtenir PDFs en Base64
        return new String[]{}; // Torna els PDFs en Base64
    }
}
