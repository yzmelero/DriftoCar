/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MongoDB;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 *
 * @author Anna
 */
@Document(collection = "documentacio_client")
public class DocumentacioClient {
    
    
    @Id
    private String dni;
    
    private String nom;
    
    private String cognoms;
    
    private String email;

    private String llicencia;
     
    private LocalDate llicCaducitat;
    
    private LocalDate dniCaducitat;
    
    private int numTarjetaCredit;
    
    private String adreca;
    
    private String contrasenya;
    
    //si es 1 sera premium
    private boolean reputacio;
    

}