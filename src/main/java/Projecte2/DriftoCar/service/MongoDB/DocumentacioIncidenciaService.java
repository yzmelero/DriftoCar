/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MongoDB;

import Projecte2.DriftoCar.entity.MongoDB.DocumentacioIncidencia;
import Projecte2.DriftoCar.repository.MongoDB.DocumentacioIncidenciaRepository;
import java.io.IOException;
import org.bson.types.Binary;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author Anna
 */

@Service
public class DocumentacioIncidenciaService {

    @Autowired
    private DocumentacioIncidenciaRepository documentacioIncidenciaRepository;
    
    public DocumentacioIncidencia guardarDocumentacio(String text, MultipartFile[] fotos, MultipartFile[] pdf) throws IOException {
        // Crear l'objecte DocumentacioIncidencia
        DocumentacioIncidencia documentacio = new DocumentacioIncidencia();
        documentacio.setText(text); // Establir el text redactat per l'usuari
        documentacio.setFotos(convertirMultipartsABinary(fotos)); // Convertir les fotos a Binary
        documentacio.setPdf(convertirMultipartsABinary(pdf)); // Convertir els PDFs a Binary

        // Guardar la documentació a MongoDB
        return documentacioIncidenciaRepository.save(documentacio);
    }

    // Convertir los archivos MultipartFile[] a Binary[]
    public Binary[] convertirMultipartsABinary(MultipartFile[] files) throws IOException {
        // Crear un array de Binary amb la mateixa longitud que els fitxers
        Binary[] binaryFiles = new Binary[files.length];
        // Convertir cada fitxer a Binary
        for (int i = 0; i < files.length; i++) {
            binaryFiles[i] = new Binary(files[i].getBytes());  // Llegir el contingut del fitxer i convertir-lo
        }
        // Retornar l'array de Binary
        return binaryFiles;
    }
}