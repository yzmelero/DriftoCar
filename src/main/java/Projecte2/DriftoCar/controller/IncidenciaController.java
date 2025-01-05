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
 * Controlador per gestionar les operacions relacionades amb les incidències
 * dels vehicles.
 * 
 * Aquesta classe proporciona funcionalitats per:
 * - Llistar totes les incidències registrades.
 * - Obtenir vehicles sense incidències actives.
 * - Obrir noves incidències per a un vehicle específic.
 * - Desar les incidències juntament amb la seva documentació associada (fotos i
 * PDFs).
 * - Tancar incidències i registrar l'històric.
 * - Mostrar el detall d'una incidència, incloent la documentació associada.
 * - Descarregar documents PDF associats a una incidència.
 * - Visualitzar l'historial d'incidències d'un vehicle.
 * 
 * Els mètodes d'aquesta classe interactuen amb els serveis per gestionar la
 * lògica de negoci de les incidències,
 * vehicles, documentació associada i historial d'incidències.
 * 
 * Dependències:
 * - {@code VehicleService}: Per obtenir informació sobre els vehicles.
 * - {@code IncidenciaService}: Per gestionar la lògica de les incidències
 * (obrir, tancar, obtenir).
 * - {@code DocumentacioIncidenciaService}: Per gestionar la documentació
 * associada a les incidències (fotos, PDFs).
 * - {@code HistoricIncidenciesService}: Per gestionar l'historial de les
 * incidències.
 * 
 */
@Controller
@RequestMapping("/incidencia")
@Scope("session")
public class IncidenciaController {

    private static final Logger log = LoggerFactory.getLogger(IncidenciaController.class);

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

    /**
     * Mostra la llista d'incidències.
     *
     * @param model El model que es passarà a la vista.
     * @return El nom de la vista de llista d'incidències.
     */
    @GetMapping("/llistar-incidencies")
    public String llistarIncidencies(@RequestParam(value = "matricula", required = false) String matricula,
            @RequestParam(value = "localitzacio.codiPostal", required = false) String codiPostal,
            @RequestParam(value = "estat", required = false) Boolean estat,
            Model model) {

        log.info("Mostrant la llista d'incidències.");
        matricula = (matricula != null && matricula.isEmpty()) ? null : matricula;
        codiPostal = (codiPostal != null && codiPostal.isEmpty()) ? null : codiPostal;

        List<Incidencia> llistarIncidencies = incidenciaService.filtrarIncidencies(matricula, codiPostal, estat);

        model.addAttribute("incidencies", llistarIncidencies);
        log.info("Llista d'incidències carregada correctament.");

        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions);
        return "incidencia-llistar";
    }

    /**
     * Mostra la llista de vehicles sense incidències actives.
     *
     * @param model           El model per a la vista.
     * @param searchMatricula Paràmetre de cerca per matrícula del vehicle.
     * @return La vista amb els vehicles sense incidències.
     */
    @GetMapping("/llistar")
    public String llistarVehiclesSenseIncidencies(Model model,
            @RequestParam(value = "searchMatricula", required = false) String searchMatricula) {

        log.info("Cercant vehicles sense incidències actives.");
        // Obtenim els vehicles sense incidències actives
        List<Vehicle> vehiclesSenseIncidencies = incidenciaService
                .llistarVehiclesSenseIncidenciesActives(searchMatricula);

        // Afegim els vehicles al model
        model.addAttribute("vehicles", vehiclesSenseIncidencies);
        model.addAttribute("searchMatricula", searchMatricula);

        log.info("Vehicles sense incidències carregats correctament.");
        return "incidencia-cerca-vehicle"; // La vista que mostrarà la llista
    }

    /**
     * Obre una nova incidència per a un vehicle específic.
     *
     * @param matricula La matrícula del vehicle.
     * @param model     El model que es passarà a la vista.
     * @return El nom de la vista per crear la incidència.
     */
    @GetMapping("/obrir/{matricula}")
    public String obrirIncidencia(@PathVariable String matricula, Model model) {

        log.info("Obrint incidència per al vehicle amb matrícula: {}", matricula);
        // Obtenir el vehicle per matrícula
        Vehicle vehicle = vehicleService.obtenirVehicleMatricula(matricula);

        if (vehicle == null) {
            log.warn("Vehicle amb matrícula {} no trobat.", matricula);
            model.addAttribute("error", "El vehicle amb la matrícula " + matricula + " no existeix.");
            return "redirect:/incidencia/llistar"; // Redirigeix si el vehicle no es troba
        }

        // Crear una nova incidència per al vehicle
        Incidencia incidencia = new Incidencia();
        incidencia.setMatricula(vehicle);
        model.addAttribute("incidencia", incidencia);

        log.info("Formulari per obrir incidència carregat per al vehicle amb matrícula: {}", matricula);
        return "incidencia-alta"; // Mostrar el formulari per crear la incidència
    }

    /**
     * Desa una nova incidència i la seva documentació associada.
     *
     * @param incidencia         La incidència a guardar.
     * @param fotos              Arxius de fotos associats a la incidència.
     * @param pdf                Arxius PDF associats a la incidència.
     * @param text               Descripció de la incidència.
     * @param redirectAttributes Atributs per redirigir amb missatges d'èxit o
     *                           error.
     * @return La vista de redirecció amb l'estat de l'operació.
     */
    @PostMapping("/obrir")
    public String guardarIncidencia(@ModelAttribute("incidencia") Incidencia incidencia,
            @RequestParam("fotos") MultipartFile[] fotos,
            @RequestParam("pdf") MultipartFile[] pdf,
            @RequestParam("text") String text,
            RedirectAttributes redirectAttributes) {
        try {
            log.info("Desant la nova incidència.");

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
            log.info("Incidència oberta i documentació associada desada correctament.");
            return "redirect:/incidencia/llistar-incidencies";
        } catch (RuntimeException | IOException e) {
            log.error("Error en obrir la incidència: {}", e.getMessage());
            // Missatge d'error
            redirectAttributes.addFlashAttribute("error", "Error en obrir la incidència: " + e.getMessage());
            return "redirect:/incidencia/llistar-incidencies";
        }
    }

    /**
     * Mostra el formulari per modificar una incidència existent.
     *
     * @param id    l'identificador de la incidència a modificar.
     * @param model el model utilitzat per passar dades a la vista.
     * @return el nom de la vista del formulari de modificació.
     */
    @GetMapping("/modificar/{id}")
    public String modificarIncidenciaForm(@PathVariable Long id, Model model) {
        log.info("Obrint formulari per modificar la incidència amb ID: {}", id);

        // Obtenir la incidència existent
        Incidencia incidencia = incidenciaService.obtenirIncidenciaPerId(id);
        if (incidencia == null) {
            log.warn("No s'ha trobat cap incidència amb ID: {}", id);
            model.addAttribute("error", "La incidència amb ID " + id + " no existeix.");
            return "redirect:/incidencia/llistar-incidencies";
        }

        // Afegir la incidència i la documentació existent al model
        incidencia.getMatricula();
        log.info("Incidència trobada amb matrícula: {}", incidencia.getMatricula().getMatricula());
        model.addAttribute("incidencia", incidencia);

        DocumentacioIncidencia documentacioExistent = documentacioIncidenciaService
                .obtenirDocumentacioAmbBase64PerIncidencia(id.toString());
        model.addAttribute("documentacio", documentacioExistent);

        log.info("Formulari de modificació carregat correctament per la incidència amb ID: {}", id);
        return "incidencia-modificar"; // Vista del formulari
    }

    /**
     * Processa la modificació d'una incidència existent.
     *
     * @param incidencia l'objecte incidència modificat.
     * @param descripcio la descripció de la incidència.
     * @param fotos      les imatges associades a la incidència.
     * @param pdf        els documents PDF associats.
     * @param model      el model utilitzat per passar dades a la vista.
     * @return la vista redirigida després de la modificació.
     */
    @PostMapping("/modificar")
    public String modificarIncidencia(
            @ModelAttribute("incidencia") Incidencia incidencia,
            @RequestParam("descripcio") String descripcio,
            @RequestParam(value = "fotos", required = false) MultipartFile[] fotos,
            @RequestParam(value = "pdf", required = false) MultipartFile[] pdf,
            Model model) {
        log.info("Iniciant modificació de la incidència amb ID: {}", incidencia.getId());

        try {
            // Obtenir la incidència existent a MySQL
            Incidencia existent = incidenciaService.obtenirIncidenciaPerId(incidencia.getId());
            if (existent == null) {
                log.warn("No s'ha robat cap incidència amb ID: {}", incidencia.getId());
                model.addAttribute("error", "La incidència amb ID " + incidencia.getId() + " no existeix.");
                return "incidencia-modificar";
            }

            // Actualitzar només el motiu de la incidència
            existent.setMotiu(incidencia.getMotiu());
            incidenciaService.modificarIncidencia(existent);
            log.info("Motiu de la incidència actualitzat correctament.");

            // Obtenir o crear la documentació associada a MongoDB
            DocumentacioIncidencia documentacio = documentacioIncidenciaService
                    .obtenirDocumentacioPerId(incidencia.getId().toString());
            documentacio.setId(incidencia.getId().toString());

            // Actualitzar els camps de documentació
            try {
                if (fotos != null && fotos.length > 0 && !fotos[0].isEmpty()) {
                    documentacio.setFotos(documentacioIncidenciaService.convertirMultipartsABinary(fotos));
                    log.info("Fotos actualitzades per la incidència amb ID: {}", incidencia.getId());
                }
                if (pdf != null && pdf.length > 0 && !pdf[0].isEmpty()) {
                    documentacio.setPdf(documentacioIncidenciaService.convertirMultipartsABinary(pdf));
                    log.info("PDF actualitzat per la incidència amb ID: {}", incidencia.getId());
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

    /**
     * Tanca una incidència i crea l'entrada associada a l'historial.
     *
     * @param id                 L'ID de la incidència a tancar.
     * @param redirectAttributes Atributs per redirigir amb missatges d'èxit o
     *                           error.
     * @return La vista de redirecció amb l'estat de l'operació.
     */
    @GetMapping("/tancar/{id}")
    public String tancarIncidencia(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            log.info("Tancant la incidència amb ID: {}", id);
            // Tancar la incidència i crear la nova entrada en MongoDB
            incidenciaService.tancarIncidencia(id);

            redirectAttributes.addFlashAttribute("success", "Incidència tancada correctament.");
            log.info("Incidència amb ID: {} tancada correctament.", id);
        } catch (RuntimeException e) {
            log.error("Error en tancar la incidència: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error en tancar la incidència: " + e.getMessage());
        }
        return "redirect:/incidencia/llistar-incidencies";
    }

    /**
     * Mostra el detall d'una incidència.
     *
     * @param id    L'ID de la incidència a mostrar.
     * @param model El model per afegir atributs a la vista.
     * @return La vista amb el detall de la incidència.
     */
    @GetMapping("/detall/{id}")
    public String mostrarDetallIncidencia(@PathVariable Long id, Model model) {
        try {
            log.info("Obtenint detall de la incidència amb ID: {}", id);
            // Obtener la incidencia desde el servicio
            Incidencia incidencia = incidenciaService.obtenirIncidenciaPerId(id);

            if (incidencia == null) {
                log.warn("Incidència amb ID: {} no trobada.", id);
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

            log.info("Detall de la incidència amb ID: {} mostrat correctament.", id);
            return "documentacio-mostrar";
        } catch (RuntimeException e) {
            log.error("Error al mostrar el detall de la incidència: {}", e.getMessage());
            model.addAttribute("error", e.getMessage());
            return "redirect:/incidencia/incidencia-llistar";
        }
    }

    /**
     * Descarrega el PDF associat a la incidència.
     *
     * @param documentId L'ID del document PDF a descarregar.
     * @param response   La resposta HTTP per enviar el PDF.
     */
    @GetMapping("/descargar-pdf/{documentId}")
    public void descargarPDF(@PathVariable String documentId, HttpServletResponse response) {
        try {
            log.info("Descarregant PDF amb ID: {}", documentId);
            // Obtener el PDF desde el servicio
            byte[] pdfBytes = documentacioIncidenciaService.obtenirPdfPerId(documentId);

            // Configurar la respuesta para que sea descargado
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=document_" + documentId + ".pdf");

            // Escribir el contenido del PDF en el flujo de salida
            response.getOutputStream().write(pdfBytes);
            response.getOutputStream().flush();
            log.info("PDF amb ID: {} descarregat correctament.", documentId);
        } catch (RuntimeException | IOException e) {
            log.error("Error al descarregar el PDF: {}", e.getMessage());
            // Manejo de errores si no se encuentra el PDF o hay problemas con la escritura
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    /**
     * Mostra el historial de les incidències per matrícula.
     *
     * @param matricula La matrícula per filtrar el historial.
     * @param model     El model per afegir atributs a la vista.
     * @return La vista amb el historial de les incidències.
     */
    @GetMapping("/historial")
    public String verHistorial(@RequestParam(value = "matricula", required = false) String matricula, Model model) {
        log.info("Sol·licitud per veure l'historial de les incidències per a la matrícula: {}", matricula);
        try {
            List<HistoricIncidencies> historial = historicIncidenciesService.findByMatricula(matricula);

            // TODO Controlar que això no doni errors, codi nou.
            if (historial.isEmpty()) {
                log.warn("No s'ha trobat cap incidència per a la matrícula: {}", matricula);
            } else {
                log.info("S'ha obtingut l'historial per a la matrícula: {}", matricula);
            }

            // Pasar el historial al modelo para que sea accesible en la vista
            model.addAttribute("historial", historial);
            model.addAttribute("matricula", matricula); // Para mantener el filtro en la vista
            return "historial-incidencia"; // Nombre de la vista
        } catch (Exception e) {
            log.error("Error en obtenir l'historial de les incidències per a la matrícula: {}. Error: {}", matricula,
                    e.getMessage());
            // Manejar error si no se encuentra el historial
            model.addAttribute("error", "No s'ha pogut obtenir el historial de les incidències.");
            return "error"; // Vista de error
        }
    }
}
