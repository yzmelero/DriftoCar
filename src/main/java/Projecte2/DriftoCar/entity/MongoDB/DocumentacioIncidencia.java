/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.entity.MongoDB;

import jakarta.persistence.Transient;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Classe que representa la documentació associada a una incidència.
 */
@Document(collection = "documentacio_incidencia")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentacioIncidencia {

    /**
     * Identificador únic de la documentació de la incidència.
     */
    @Id
    private String id;
    
    /**
     * Fotos relacionades amb la incidència, emmagatzemades com a array de bytes.
     */
    private Binary[] fotos;

    /**
     * Text descriptiu de la incidència.
     */
    private String text;

    /**
     * Fitxers PDF relacionats amb la incidència, emmagatzemats com a array de bytes.
     */
    private Binary[] pdf;
    
    /**
     * Fotos en format Base64 per ser utilitzades en representacions no persistents.
     */
    @Transient
    private String[] fotosBase64; // Atribut per les imatges convertides

    /**
     * PDFs en format Base64 per ser utilitzats en representacions no persistents.
     */
    @Transient
    private String[] pdfBase64; // Atribut per els PDFs convertides
    
}
