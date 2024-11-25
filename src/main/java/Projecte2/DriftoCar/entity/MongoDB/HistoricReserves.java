/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MongoDB;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


/**
 *
 * @author Anna
 */
@Document(collection = "historic_reserves")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricReserves {

    @Id
    private Long idReserva;

    private String nomClient;
    
    private String DNI;
    
    private String cognomClient;
    
    private String matricula;
    
    private LocalDate dataInici;
    private LocalDate dataFi;
    private double totalCost;
    private double fianca;
    private boolean estat;

  
}
