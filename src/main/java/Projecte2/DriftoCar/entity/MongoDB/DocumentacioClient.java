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

    private String usuari;
    
    private String contrasenya;
    
    private String nom;
    
    private String cognoms;
    
    private String email;

    private String llicencia;
     
    private LocalDate llicCaducitat;
    
    private LocalDate dniCaducitat;
    
    private int numTarjetaCredit;
    
    private String adreca;
    
    //si es 1 sera premium
    private boolean reputacio;
    

}