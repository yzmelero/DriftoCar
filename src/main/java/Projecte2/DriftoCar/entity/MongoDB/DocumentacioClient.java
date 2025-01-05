/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MongoDB;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
/**
 * Classe que representa la documentació associada a un client.
 */
@Document(collection = "documentacio_client")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentacioClient {
    
    /**
     * DNI del client, que actua com a identificador únic.
     */ 
    @Id
    private String dni;
    
    /**
     * Imatges del DNI del client, emmagatzemades com a array de bytes.
     */
    private Binary[] imatgeDni;
    
    /**
     * Imatges de la llicència del client, emmagatzemades com a array de bytes.
     */
    private Binary[] imatgeLlicencia;

}