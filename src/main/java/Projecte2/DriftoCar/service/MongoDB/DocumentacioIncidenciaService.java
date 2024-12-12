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

    public void guardarDocumentacio(DocumentacioIncidencia documentacioIncidencia) {
        try {
            documentacioIncidenciaRepository.save(documentacioIncidencia);
            System.out.println("Documentación guardada en MongoDB: " + documentacioIncidencia);
        } catch (Exception e) {
            System.out.println("Error al guardar la documentación en MongoDB: " + e.getMessage());
        }
    }

    // Convertir los archivos MultipartFile[] a Binary[]
    public Binary[] convertirMultipartsABinary(MultipartFile[] files) throws IOException {
        Binary[] binaryFiles = new Binary[files.length];
        for (int i = 0; i < files.length; i++) {
            binaryFiles[i] = new Binary(files[i].getBytes());  // Convertir cada archivo a Binary
        }
        return binaryFiles;
    }
}