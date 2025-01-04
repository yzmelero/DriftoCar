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
 * Classe que representa l'històric de les reserves realitzades.
 */
@Document(collection = "historic_reserves")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricReserves {

    /**
     * Identificador únic de la reserva.
     */
    @Id
    private String idReserva;
    
    /**
     * Nom del client que ha realitzat la reserva.
     */
    private String nomClient;

    /**
     * DNI del client que ha realitzat la reserva.
     */
    private String DNI;

    /**
     * Cognom del client que ha realitzat la reserva.
     */
    private String cognomClient;

    /**
     * Matrícula del vehicle reservat.
     */
    private String matricula;

    /**
     * Data d'inici de la reserva.
     */
    private LocalDate dataInici;

    /**
     * Data de finalització de la reserva.
     */
    
    private LocalDate dataFi;

    /**
     * Cost total de la reserva.
     */
    
    private double totalCost;

    /**
     * Fiança pagada per la reserva.
     */
    
    private double fianca;

    /**
     * Estat de la reserva (true = activa, false = finalitzada o cancel·lada).
     */
    
    private boolean estat;

}
