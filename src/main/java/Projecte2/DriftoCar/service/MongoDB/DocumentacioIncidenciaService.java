/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.service.MongoDB;

import Projecte2.DriftoCar.entity.MongoDB.DocumentacioIncidencia;
import Projecte2.DriftoCar.repository.MongoDB.DocumentacioIncidenciaRepository;
import java.io.IOException;
import java.util.Base64;
import java.util.List;
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

    // Métode per guardar la documentació (ja existent)
    public DocumentacioIncidencia guardarDocumentacio(Long incidenciaId, String text, MultipartFile[] fotos, MultipartFile[] pdf) throws IOException {
        DocumentacioIncidencia documentacio = new DocumentacioIncidencia();
        documentacio.setIncidenciaId(incidenciaId); // Asignar incidenciaId al document
        documentacio.setText(text);
        documentacio.setFotos(convertirMultipartsABinary(fotos));
        documentacio.setPdf(convertirMultipartsABinary(pdf));

        return documentacioIncidenciaRepository.save(documentacio);
    }

    // Nou métode per obtenir i processar la documentació
    public List<DocumentacioIncidencia> obtenirDocumentacioAmbBase64() {
        // Obtenim tota la documentació desde MongoDB
        List<DocumentacioIncidencia> documentacioList = documentacioIncidenciaRepository.findAll();

        // Procesem les imatges i PDFs, convertim a Base64
        for (DocumentacioIncidencia doc : documentacioList) {
            if (doc.getFotos() != null) {
                doc.setFotosBase64(convertirBinaryABase64(doc.getFotos())); // Convertim les imatges
            }
            if (doc.getPdf() != null) {
                doc.setPdfBase64(convertirBinaryABase64(doc.getPdf())); // Convertim els PDFs
            }
        }

        return documentacioList;
    }

    // Conversió d'archius Binary[] a Base64
    public String[] convertirBinaryABase64(Binary[] binaries) {
        if (binaries == null) {
            return null;
        }

        String[] base64Array = new String[binaries.length];
        for (int i = 0; i < binaries.length; i++) {
            base64Array[i] = Base64.getEncoder().encodeToString(binaries[i].getData());
        }
        return base64Array;
    }

    // Conversió de MultipartFile[] a Binary[]
    public Binary[] convertirMultipartsABinary(MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            return null;
        }

        Binary[] binaryFiles = new Binary[files.length];
        for (int i = 0; i < files.length; i++) {
            binaryFiles[i] = new Binary(files[i].getBytes());
        }
        return binaryFiles;
    }
    
    // Métode per obtenir el PDF d'una documentació
    public byte[] obtenirPdfPerId(String documentacioId) {
        // Obtenemos el documento desde la base de datos MongoDB
        DocumentacioIncidencia documentacio = documentacioIncidenciaRepository.findById(documentacioId).orElse(null);

        if (documentacio == null || documentacio.getPdf() == null) {
            throw new RuntimeException("No s'ha trobat cap PDF associat amb aquest document.");
        }

        // Devolvemos el PDF en formato de array de bytes
        return documentacio.getPdf()[0].getData(); // Assuming single PDF per document
    }
    
    public List<DocumentacioIncidencia> obtenirTotaDocumentacio() {
        return documentacioIncidenciaRepository.findAll();
    }

    public List<DocumentacioIncidencia> obtenirDocumentacioPerIncidenciaId(Long incidenciaId) {
        return documentacioIncidenciaRepository.findByIncidenciaId(incidenciaId);
    }

    public DocumentacioIncidencia obtenirDocumentacioPerId(String documentacioId) {
        return documentacioIncidenciaRepository.findById(documentacioId).orElse(null);
    }

}
