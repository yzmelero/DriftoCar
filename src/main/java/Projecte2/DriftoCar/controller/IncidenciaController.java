/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MongoDB.DocumentacioIncidencia;
import Projecte2.DriftoCar.entity.MongoDB.HistoricIncidencies;
import Projecte2.DriftoCar.entity.MySQL.Incidencia;
import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.repository.MongoDB.DocumentacioIncidenciaRepository;
import Projecte2.DriftoCar.service.MongoDB.DocumentacioIncidenciaService;
import Projecte2.DriftoCar.service.MongoDB.HistoricIncidenciesService;
import Projecte2.DriftoCar.service.MySQL.IncidenciaService;
import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;
import Projecte2.DriftoCar.service.MySQL.VehicleService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author Hector
 */
@Controller
@RequestMapping("/incidencia")
@Scope("session")
public class IncidenciaController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private IncidenciaService incidenciaService;

    @Autowired
    private LocalitzacioService localitzacioService;

    @Autowired
    private DocumentacioIncidenciaService documentacioIncidenciaService;

    @Autowired
    private DocumentacioIncidenciaRepository documentacioIncidenciaRepository;

    @Autowired
    private HistoricIncidenciesService historicIncidenciesService;

    Logger log = LoggerFactory.getLogger(IncidenciaController.class);

    @GetMapping("/llistar-incidencies")
    public String llistarIncidencies(@RequestParam(value = "matricula", required = false) String matricula,
            @RequestParam(value = "localitzacio.codiPostal", required = false) String codiPostal,
            @RequestParam(value = "estat", required = false) Boolean estat,
            Model model) {

        matricula = (matricula != null && matricula.isEmpty()) ? null : matricula;
        codiPostal = (codiPostal != null && codiPostal.isEmpty()) ? null : codiPostal;

        List<Incidencia> llistarIncidencies = incidenciaService.filtrarIncidencies(matricula, codiPostal, estat);

        model.addAttribute("incidencies", llistarIncidencies);

        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions)
        ;
        return "incidencia-llistar";
    }

    @GetMapping("/llistar")
    public String llistarVehiclesSenseIncidencies(Model model,
            @RequestParam(value = "searchMatricula", required = false) String searchMatricula) {
        // Obtenim els vehicles sense incidències actives
        List<Vehicle> vehiclesSenseIncidencies = incidenciaService.llistarVehiclesSenseIncidenciesActives(searchMatricula);

        // Afegim els vehicles al model
        model.addAttribute("vehicles", vehiclesSenseIncidencies);
        model.addAttribute("searchMatricula", searchMatricula);

        return "incidencia-cerca-vehicle"; // La vista que mostrarà la llista
    }

    // Mostrar el formulario de creación de la incidencia
    @GetMapping("/obrir/{matricula}")
    public String obrirIncidencia(@PathVariable String matricula, Model model) {
        // Obtenir el vehicle per matrícula
        Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);

        if (vehicle == null) {
            model.addAttribute("error", "El vehicle amb la matrícula " + matricula + " no existeix.");
            return "redirect:/incidencia/llistar"; // Redirigeix si el vehicle no es troba
        }

        // Crear una nova incidència per al vehicle
        Incidencia incidencia = new Incidencia();
        incidencia.setMatricula(vehicle);
        model.addAttribute("incidencia", incidencia);

        return "incidencia-alta"; // Mostrar el formulari per crear la incidència
    }

    @PostMapping("/obrir")
    public String guardarIncidencia(@ModelAttribute("incidencia") Incidencia incidencia,
            @RequestParam("fotos") MultipartFile[] fotos,
            @RequestParam("pdf") MultipartFile[] pdf,
            @RequestParam("text") String text,
            RedirectAttributes redirectAttributes) {
        try {
            // Desar la incidencia en MySQL
            incidenciaService.obrirIncidencia(incidencia);

            // Obtenir l'ID de la incidencia recent creada
            Long incidenciaId = incidencia.getId();

            // Guardar el historial de la incidencia en MongoDB
            historicIncidenciesService.guardarHistoricIncidencia(incidencia); // Aquí es guarda a l'historial

            // Desar la documentación associada en MongoDB
            documentacioIncidenciaService.guardarDocumentacio(incidenciaId, text, fotos, pdf);

            // Missatge d'éxit
            redirectAttributes.addFlashAttribute("success", "Incidència oberta correctament amb documentació.");
            return "redirect:/incidencia/llistar-incidencies";
        } catch (RuntimeException | IOException e) {
            // Missatge d'error
            redirectAttributes.addFlashAttribute("error", "Error en obrir la incidència: " + e.getMessage());
            return "redirect:/incidencia/llistar-incidencies";
        }
    }

    // Mostrar el formulari per modificar una incidència
    @GetMapping("/modificar/{id}")
    public String modificarIncidenciaForm(@PathVariable Long id, Model model) {
        // Obtenir la incidència existent
        Incidencia incidencia = incidenciaService.obtenirIncidenciaPerId(id);
        if (incidencia == null) {
            model.addAttribute("error", "La incidència amb ID " + id + " no existeix.");
            return "redirect:/incidencia/llistar-incidencies";
        }

        // Afegir la incidència i la documentació existent al model
        incidencia.getMatricula();
        log.info(incidencia.getMatricula().getMatricula());
        model.addAttribute("incidencia", incidencia);

        DocumentacioIncidencia documentacio = documentacioIncidenciaService.obtenirDocumentacioPerId(id.toString());
        DocumentacioIncidencia documentacioExistent = documentacioIncidenciaService
                .obtenirDocumentacioAmbBase64PerIncidencia(id.toString());
        model.addAttribute("documentacio", documentacioExistent);

        return "incidencia-modificar"; // Vista del formulari
    }

    @PostMapping("/modificar")
    public String modificarIncidencia(
            @ModelAttribute("incidencia") Incidencia incidencia,
            @RequestParam("descripcio") String descripcio,
            @RequestParam(value = "fotos", required = false) MultipartFile[] fotos,
            @RequestParam(value = "pdf", required = false) MultipartFile[] pdf,
            Model model) {
            log.info(incidencia.getMatricula().getMatricula());
        try {
            // Obtenir la incidència existent a MySQL
            Incidencia existent = incidenciaService.obtenirIncidenciaPerId(incidencia.getId());
            if (existent == null) {
                model.addAttribute("error", "La incidència amb ID " + incidencia.getId() + " no existeix.");
                return "incidencia-modificar";
            }

            // Actualitzar només el motiu de la incidència
            existent.setMotiu(incidencia.getMotiu());
            incidenciaService.modificarIncidencia(existent);

            // Obtenir o crear la documentació associada a MongoDB
            DocumentacioIncidencia documentacio = documentacioIncidenciaService.obtenirDocumentacioPerId(incidencia.getId().toString());
            documentacio.setId(incidencia.getId().toString());

            // Actualitzar els camps de documentació
            try {
                if (fotos != null && fotos.length > 0 && !fotos[0].isEmpty()) {
                    documentacio.setFotos(documentacioIncidenciaService.convertirMultipartsABinary(fotos));
                }
                if (pdf != null && pdf.length > 0 && !pdf[0].isEmpty()) {
                    documentacio.setPdf(documentacioIncidenciaService.convertirMultipartsABinary(pdf));
                }
                documentacio.setText(descripcio);                
            } catch (IOException e) {
                model.addAttribute("error", "Error al carregar els fitxers.");
                return "incidencia-modificar";
            }

            // Guardar la documentació a MongoDB
            documentacioIncidenciaRepository.save(documentacio);

            model.addAttribute("success", "La incidència s'ha modificat correctament.");
            log.info("Incidència modificada correctament: {}", incidencia.getId());
        } catch (Exception e) {
            model.addAttribute("error", "Error en modificar la incidència: " + e.getMessage());
            log.error("Error en modificar la incidència: {}", e.getMessage());
            return "incidencia-modificar";
        }

        return "redirect:/incidencia/llistar-incidencies";
    }

    @GetMapping("/tancar/{id}")
    public String tancarIncidencia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            // Tancar la incidència i crear la nova entrada en MongoDB
            incidenciaService.tancarIncidencia(id);

            redirectAttributes.addFlashAttribute("success", "Incidència tancada correctament.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Error en tancar la incidència: " + e.getMessage());
        }
        return "redirect:/incidencia/llistar-incidencies";
    }

    @GetMapping("/detall/{id}")
    public String mostrarDetallIncidencia(@PathVariable Long id, Model model) {
        try {
            // Obtener la incidencia desde el servicio
            Incidencia incidencia = incidenciaService.obtenirIncidenciaPerId(id);

            if (incidencia == null) {
                model.addAttribute("error", "Incidència no trobada.");
                return "redirect:/incidencia/incidencia-llistar";
            }

            // Validar si la descripción es null
            if (incidencia.getDescripcio() == null) {
                incidencia.setDescripcio("Descripció no disponible.");
            }

            // Obtener la documentación asociada con procesamiento Base64
            DocumentacioIncidencia documentacio = documentacioIncidenciaService
                    .obtenirDocumentacioAmbBase64PerIncidencia(id.toString());

            // Agregar atributos al modelo
            model.addAttribute("incidencia", incidencia);
            model.addAttribute("documentacio", documentacio);

            return "documentacio-mostrar";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/incidencia/incidencia-llistar";
        }
    }

    @GetMapping("/descargar-pdf/{documentId}")
    public void descargarPDF(@PathVariable String documentId, HttpServletResponse response) {
        try {
            // Obtener el PDF desde el servicio
            byte[] pdfBytes = documentacioIncidenciaService.obtenirPdfPerId(documentId);

            // Configurar la respuesta para que sea descargado
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=document_" + documentId + ".pdf");

            // Escribir el contenido del PDF en el flujo de salida
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
        } catch (RuntimeException | IOException e) {
            // Manejo de errores si no se encuentra el PDF o hay problemas con la escritura
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @GetMapping("/historial")
    public String verHistorial(@RequestParam(value = "matricula", required = false) String matricula, Model model) {
        try {
            List<HistoricIncidencies> historial = historicIncidenciesService.findByMatricula(matricula);

            // Pasar el historial al modelo para que sea accesible en la vista
            model.addAttribute("historial", historial);
            model.addAttribute("matricula", matricula); // Para mantener el filtro en la vista
            return "historial-incidencia"; // Nombre de la vista
        } catch (Exception e) {
            // Manejar error si no se encuentra el historial
            model.addAttribute("error", "No s'ha pogut obtenir el historial de les incidències.");
            return "error"; // Vista de error
        }
    }
}
