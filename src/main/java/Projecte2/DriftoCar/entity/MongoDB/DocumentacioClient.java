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
    
    private Binary[] imatgeDni;
    
    private Binary[] imatgeLlicencia;

}