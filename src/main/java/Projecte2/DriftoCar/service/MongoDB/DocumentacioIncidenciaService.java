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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * Servei per gestionar la documentació associada a les incidències.
 * Aquesta classe proporciona la lògica de negoci per afegir, modificar, eliminar
 * i recuperar documentació relacionada amb incidències al sistema.
 *
 */
@Service
public class DocumentacioIncidenciaService {

    Logger log = LoggerFactory.getLogger(DocumentacioIncidenciaService.class);

    @Autowired
    private DocumentacioIncidenciaRepository documentacioIncidenciaRepository;

    /**
     * Guarda la documentació associada a una incidència.
     *
     * @param incidenciaId ID de la incidència.
     * @param text         Text associat a la documentació.
     * @param fotos        Array de fitxers d'imatge.
     * @param pdf          Array de fitxers PDF.
     * @return Documentació guardada.
     * @throws IOException Si hi ha un error en processar els fitxers.
     */
    public DocumentacioIncidencia guardarDocumentacio(Long incidenciaId, String text, MultipartFile[] fotos,
            MultipartFile[] pdf) throws IOException {
        log.info("S'ha entrat al mètode guardarDocumentacio del Servei per a la incidència amb ID {}", incidenciaId);
        DocumentacioIncidencia documentacio = new DocumentacioIncidencia();
        documentacio.setId(incidenciaId.toString());
        documentacio.setText(text);
        documentacio.setFotos(convertirMultipartsABinary(fotos));
        documentacio.setPdf(convertirMultipartsABinary(pdf));

        log.info("S'ha guardat correctament la documentació al servei amb ID {}", incidenciaId);

        return documentacioIncidenciaRepository.save(documentacio);
    }

    /**
     * Obté i processa la documentació associada a una incidència.
     * Converteix imatges i PDFs a format Base64.
     *
     * @param id ID de la documentació.
     * @return Documentació processada amb imatges i PDFs en Base64.
     */
    public DocumentacioIncidencia obtenirDocumentacioAmbBase64PerIncidencia(String id) {
        log.info("S'ha entrat al mètode obtenirDocumentacioAmbBase64PerIncidencia per ID {}", id);

        // Reutilitzar el mètode existent per obtenir la documentació per id
        DocumentacioIncidencia documentacio = obtenirDocumentacioPerId(id);

        // Convertir fotos i PDFs a Base64
        if (documentacio != null) {
            if (documentacio.getFotos() != null) {
                documentacio.setFotosBase64(convertirBinaryABase64(documentacio.getFotos())); // Convertir fotos a
                                                                                              // Base64
                log.info("Les imatges s'han convertit a Base64 per a la documentació amb ID {}", id);
            }
            if (documentacio.getPdf() != null) {
                documentacio.setPdfBase64(convertirBinaryABase64(documentacio.getPdf())); // Convertir PDFs a Base64
                log.info("Els PDFs s'han convertit a Base64 per a la documentació amb ID {}", id);
            }
        }

        return documentacio;
    }

    /**
     * Converteix un array de dades binàries a Base64.
     *
     * @param binaries Array de dades binàries.
     * @return Array de cadenes Base64.
     */
    public String[] convertirBinaryABase64(Binary[] binaries) {
        if (binaries == null) {
            log.warn("S'ha intentat convertir un array de binaris nul a Base64.");
            return null;
        }

        String[] base64Array = new String[binaries.length];
        for (int i = 0; i < binaries.length; i++) {
            base64Array[i] = Base64.getEncoder().encodeToString(binaries[i].getData());
        }
        log.info("S'ha convertit un array de {} binaris a Base64.", binaries.length);
        return base64Array;
    }

    /**
     * Converteix un array de fitxers Multipart a dades binàries.
     *
     * @param files Array de fitxers Multipart.
     * @return Array de dades binàries.
     * @throws IOException Si hi ha un error en processar els fitxers.
     */
    public Binary[] convertirMultipartsABinary(MultipartFile[] files) throws IOException {
        if (files == null || files.length == 0) {
            log.warn("S'ha intentat convertir un array de fitxers nul o buit a binaris.");
            return null;
        }

        Binary[] binaryFiles = new Binary[files.length];
        for (int i = 0; i < files.length; i++) {
            binaryFiles[i] = new Binary(files[i].getBytes());
        }
        log.info("S'han convertit {} fitxers Multipart a binaris.", files.length);
        return binaryFiles;
    }

    /**
     * Obté un PDF associat a una documentació.
     *
     * @param documentacioId ID de la documentació.
     * @return Contingut del PDF en format byte array.
     * @throws RuntimeException Si no es troba cap PDF associat.
     */
    public byte[] obtenirPdfPerId(String documentacioId) {
        log.info("S'ha entrat al mètode obtenirPdfPerId per a la documentació amb ID {}", documentacioId);

        // Obtenemos el documento desde la base de datos MongoDB
        DocumentacioIncidencia documentacio = documentacioIncidenciaRepository.findById(documentacioId).orElse(null);

        if (documentacio == null || documentacio.getPdf() == null) {
            log.error("No s'ha trobat cap PDF associat amb la documentació ID {}", documentacioId);
            throw new RuntimeException("No s'ha trobat cap PDF associat amb aquest document.");
        }

        log.info("S'ha obtingut correctament el PDF associat a la documentació amb ID {}", documentacioId);
        // Devolvemos el PDF en formato de array de bytes
        return documentacio.getPdf()[0].getData(); // Assuming single PDF per document
    }

    /**
     * Obté tota la documentació existent.
     *
     * @return Llista de totes les documentacions.
     */
    public List<DocumentacioIncidencia> obtenirTotaDocumentacio() {
        log.info("S'ha entrat al mètode obtenirTotaDocumentacio");
        return documentacioIncidenciaRepository.findAll();
    }

    /**
     * Obté una documentació per ID.
     *
     * @param documentacioId ID de la documentació.
     * @return Documentació trobada o null si no existeix.
     */
    public DocumentacioIncidencia obtenirDocumentacioPerId(String documentacioId) {
        log.info("S'ha entrat al mètode obtenirDocumentacioPerId per ID {}", documentacioId);
        return documentacioIncidenciaRepository.findById(documentacioId).orElse(null);
    }

}
