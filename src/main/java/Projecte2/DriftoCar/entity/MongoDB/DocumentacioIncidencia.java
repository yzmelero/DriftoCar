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
 *
 * @author Anna
 */
@Document(collection = "documentacio_incidencia")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentacioIncidencia {
    @Id
    private String id;
    
    private Long incidenciaId;

    private Binary[] fotos;

    private String text;

    private Binary[] pdf;
    
    @Transient
    private String[] fotosBase64; // Atribut per les imatges convertides

    @Transient
    private String[] pdfBase64; // Atribut per els PDFs convertides
    
}
