/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MongoDB.DocumentacioIncidencia;
import Projecte2.DriftoCar.entity.MySQL.Incidencia;
import Projecte2.DriftoCar.entity.MySQL.Vehicle;
import Projecte2.DriftoCar.service.MongoDB.DocumentacioIncidenciaService;
import Projecte2.DriftoCar.service.MySQL.IncidenciaService;
import Projecte2.DriftoCar.service.MySQL.VehicleService;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
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
    private DocumentacioIncidenciaService documentacioIncidenciaService;

    @GetMapping("/llistar-incidencies")
    public String llistarIncidencies(@RequestParam(value = "matricula", required = false) String matricula,
            @RequestParam(value = "localitzacio.codiPostal", required = false) String codiPostal,
            @RequestParam(value = "estat", required = false) Boolean estat,
            Model model) {
        List<Incidencia> incidencies = incidenciaService.llistarIncidencies();

         List<Incidencia> llistarIncidencies = incidenciaService.filtrarIncidencies(matricula, codiPostal, estat);
        
        model.addAttribute("listarIncidencies", llistarIncidencies);
        model.addAttribute("incidencies", incidencies);
        return "incidencia-llistar";
    }

    @GetMapping("/llistar")
    public String llistarVehiclesSenseIncidencies(Model model) {
        // Obtenim els vehicles sense incidències actives
        List<Vehicle> vehiclesSenseIncidencies = incidenciaService.llistarVehiclesSenseIncidenciesActives();

        // Afegim els vehicles al model
        model.addAttribute("vehicles", vehiclesSenseIncidencies);

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

    @GetMapping("/tancar/{id}")
    public String tancarIncidencia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
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
                return "redirect:/incidencia/llistar-incidencies";
            }

            // Validar si la descripción es null
            if (incidencia.getDescripcio() == null) {
                incidencia.setDescripcio("Descripció no disponible.");
            }

            // Obtener la documentación asociada con procesamiento Base64
            List<DocumentacioIncidencia> documentacioList = documentacioIncidenciaService.obtenirDocumentacioAmbBase64PerIncidencia(id);

            // Agregar atributos al modelo
            model.addAttribute("incidencia", incidencia);
            model.addAttribute("documentacioList", documentacioList);

            return "documentacio-mostrar";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "redirect:/incidencia/llistar-incidencies";
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
}
