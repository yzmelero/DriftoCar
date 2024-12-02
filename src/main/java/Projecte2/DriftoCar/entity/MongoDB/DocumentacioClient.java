/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MongoDB;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
/**
 *
 * @author Anna
 */
@Document(collection = "documentacio_client")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentacioClient {
    
    
    @Id
    private String dni;
        
    private String nom;
    
    private String cognoms;
    
    private String contrasenya;
    
    private String email; //Sera el nom d'usuari

    private String llicencia;
    
    private String tipusLlicencia;
    
    private LocalDate llicCaducitat;
    
    private LocalDate dniCaducitat;
    
    private Binary[] imatgeDni;
    
    private Binary[] imatgeLlicencia;
    
    private String numTarjetaCredit;
    
    private String adreca;

}