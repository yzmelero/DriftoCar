/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MongoDB;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Anna
 */
@Document(collection = "historic_incidencies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricIncidencies {
    @Id
    private String id;
    
    // 1 = oberta, 0 = tancada
    private boolean estat;

    private String motiu;

    @Field(name="data_inici_incidencia")
    private LocalDateTime dataIniciIncidencia;
    
    @Field(name="data_fi_incidencia")
    private LocalDateTime dataFiIncidencia;

    private String matricula;
}
