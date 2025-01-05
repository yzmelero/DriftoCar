/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MongoDB;

import java.time.LocalDateTime;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe que representa l'històric de les incidències registrades.
 */
@Document(collection = "historic_incidencies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricIncidencies {
    /**
     * Identificador únic de l'històric de la incidència.
     */
    @Id
    private String id;
    
    /**
     * Estat de la incidència (1 = oberta, 0 = tancada).
     */
    private boolean estat;

    /**
     * Motiu o descripció de la incidència.
     */
    private String motiu;

    /**
     * Data i hora en què la incidència va ser registrada.
     */
    @Field(name="data_inici_incidencia")
    private LocalDateTime dataIniciIncidencia;
    
    /**
     * Data i hora en què la incidència es va tancar.
     */
    @Field(name="data_fi_incidencia")
    private LocalDateTime dataFiIncidencia;

    /**
     * Matrícula del vehicle afectat per la incidència.
     */
    private String matricula;

}
