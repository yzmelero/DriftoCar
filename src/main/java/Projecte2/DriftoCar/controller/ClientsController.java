/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MongoDB.DocumentacioClient;
import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.repository.MongoDB.DocumentacioClientRepository;
import Projecte2.DriftoCar.service.MySQL.ClientService;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controlador per gestionar les operacions relacionades amb els clients.
 * 
 * Aquesta classe proporciona funcionalitats per:
 * - Llistar clients amb filtres de cerca.
 * - Modificar informació dels clients.
 * - Esborrar clients.
 * - Consultar detalls d'un client.
 * - Validar i activar comptes de clients pendents.
 * 
 * Els mètodes inclouen validacions de dades, tractament de fitxers (DNI i
 * llicències)
 * i interacció amb els serveis i repositoris necessaris.
 * 
 * Dependències:
 * - {@code ClientService}: Per gestionar la lògica de negoci dels clients.
 * - {@code PasswordEncoder}: Per gestionar l'encriptació de contrasenyes.
 * - {@code DocumentacioClientRepository}: Per gestionar la documentació
 * associada als clients.
 * 
 */
@Controller
@RequestMapping("/clients")
@Scope("session")
public class ClientsController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DocumentacioClientRepository documentacioClientRepository;

    Logger log = LoggerFactory.getLogger(ClientService.class);

    /**
     * Llista els clients. Permet buscar clients amb filtres específics.
     * 
     * @param searchCognoms      Cognoms del client a cercar (opcional).
     * @param searchNacionalitat Nacionalitat del client a cercar (opcional).
     * @param searchTelefon      Telèfon del client a cercar (opcional).
     * @param searchEmail        Email del client a cercar (opcional).
     * @param model              Model per passar dades a la vista.
     * @return El nom de la vista amb la llista de clients.
     */
    @GetMapping("/llistar")
    public String llistarClients(@RequestParam(value = "searchCognoms", required = false) String searchCognoms,
            @RequestParam(value = "searchNacionalitat", required = false) String searchNacionalitat,
            @RequestParam(value = "searchTelefon", required = false) String searchTelefon,
            @RequestParam(value = "searchEmail", required = false) String searchEmail,
            Model model) {

        log.info("S'està llistant els clients amb filtres: cognoms={}, nacionalitat={}, telefon={}, email={}",
                searchCognoms, searchNacionalitat, searchTelefon, searchEmail);

        List<Client> clients;

        if ((searchCognoms != null && !searchCognoms.isEmpty())
                || (searchNacionalitat != null && !searchNacionalitat.isEmpty())
                || (searchTelefon != null && !searchTelefon.isEmpty())
                || (searchEmail != null && !searchEmail.isEmpty())) {
            clients = clientService.cercarClients(searchCognoms, searchNacionalitat, searchTelefon, searchEmail);
            log.info("S'han trobat {} clients amb els filtres aplicats.", clients.size());
        } else {
            clients = clientService.llistarClients();
            log.info("Llistant tots els clients");
        }

        model.addAttribute("clients", clients);
        model.addAttribute("searchCognoms", searchCognoms);
        model.addAttribute("searchNacionalitat", searchNacionalitat);
        model.addAttribute("searchTelefon", searchTelefon);
        model.addAttribute("searchEmail", searchEmail);

        return "client-llistar";

    }

    /**
     * Esborra un client pel seu DNI.
     * 
     * @param dni                El DNI del client a esborrar.
     * @param client             El client a esborrar.
     * @param redirectAttributes Atributs per passar missatges de redirecció.
     * @return Redirecció a la vista de llista de clients.
     */
    @GetMapping("/esborrar/{dni}")
    public String esborrarClients(@PathVariable("dni") String dni, Client client,
            RedirectAttributes redirectAttributes) {

        log.info("S'està esborrant el client amb DNI: {}", dni);
        try {
            clientService.baixaClient(client);
            // TODO Revisar què fa aquest redirectAttributes
            redirectAttributes.addFlashAttribute("success", "El client s'ha esborrat correctament.");
            log.info("El client amb DNI {} s'ha esborrat correctament.", dni);
        } catch (Exception e) {
            log.error("Error en esborrar el client amb DNI {}: {}", dni, e.getMessage());
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/clients/llistar";

    }

    /**
     * Carrega el formulari per modificar un client.
     * 
     * @param dni   El DNI del client a modificar.
     * @param model Model per passar dades a la vista.
     * @return El nom de la vista amb el formulari de modificació.
     */
    @GetMapping("/modificar/{dni}")
    public String modificarClients(@PathVariable("dni") String dni, Model model) {

        log.info("S'està accedint al formulari de modificació per al client amb DNI: {}", dni);
        Client client = clientService.obtenirClientPerDni(dni);
        if (client == null) {
            log.error("No existeix cap client amb aquest DNI: {}", dni);
            throw new RuntimeException("No existeix cap client amb aquest DNI.");

        }
        // Recuperar la documentación del cliente en MongoDB
        Optional<DocumentacioClient> docOpt = documentacioClientRepository.findById(dni);
        String imatgeDniBase64 = null;
        String imatgeLlicenciaBase64 = null;

        if (docOpt.isPresent()) {
            DocumentacioClient doc = docOpt.get();
            if (doc.getImatgeDni() != null && doc.getImatgeDni().length > 0) {
                imatgeDniBase64 = Base64.getEncoder().encodeToString(doc.getImatgeDni()[0].getData());
            }
            if (doc.getImatgeLlicencia() != null && doc.getImatgeLlicencia().length > 0) {
                imatgeLlicenciaBase64 = Base64.getEncoder().encodeToString(doc.getImatgeLlicencia()[0].getData());
            }
        }

        model.addAttribute("client", client);
        model.addAttribute("imatgeDni", imatgeDniBase64);
        model.addAttribute("imatgeLlicencia", imatgeLlicenciaBase64);

        log.info("Caducitat llicència al model: {}", client.getLlicCaducitat());
        log.info("Caducitat DNI al model: {}", client.getDniCaducitat());
        log.info("Dades carregades per al client amb DNI {}: {}", dni, client);
        return "client-modificar";
    }

    /**
     * Desa els canvis realitzats en un client existent.
     *
     * @param client              El client modificat.
     * @param imatgeDniFile       Arxiu de la imatge del DNI del client.
     * @param imatgeLlicenciaFile Arxiu de la imatge de la llicència del client.
     * @param model               Model per passar dades a la vista.
     * @return Redirecció a la vista de llista de clients si té èxit o torna al
     *         formulari en cas d'error.
     */
    @PostMapping("/modificar")
    public String guardarClientModificat(@ModelAttribute("client") Client client,
            @RequestParam("imatgeDni") MultipartFile imatgeDniFile,
            @RequestParam("imatgeLlicencia") MultipartFile imatgeLlicenciaFile,
            Model model) {

        Client existent = clientService.obtenirClientPerDni(client.getDni());
        if (client.getNacionalitat() == null || client.getNacionalitat().isEmpty()) {
            client.setNacionalitat(existent.getNacionalitat());
        }

        LocalDate currentDate = LocalDate.now();
        LocalDate maxDate = currentDate.plusYears(50);

        if (client.getDniCaducitat().isBefore(currentDate) || client.getDniCaducitat().isAfter(maxDate)) {
            log.warn("Data d'expiració del DNI no vàlida: {}", client.getDniCaducitat());
            model.addAttribute("error", "La data d'expiració del DNI no és vàlida.");
            model.addAttribute("client", client);
            return "client-modificar";
        }

        if (client.getLlicCaducitat().isBefore(currentDate) || client.getLlicCaducitat().isAfter(maxDate)) {
            log.warn("Data d'expiració de la llicència no vàlida: {}", client.getLlicCaducitat());
            model.addAttribute("error", "La data d'expiració de la llicència no és vàlida.");
            model.addAttribute("client", client);
            return "client-modificar";
        }

        if (client.getContrasenya() == null || client.getContrasenya().isEmpty()) {
            client.setContrasenya(existent.getContrasenya());
        } else {
            // Si se ha proporcionado una nueva contraseña, encriptarla
            String contrasenyaEncriptada = passwordEncoder.encode(client.getContrasenya());
            client.setContrasenya(contrasenyaEncriptada);
            log.info("Contrasenya actualitzada per al client amb DNI: {}", client.getDni());
        }

        Optional<DocumentacioClient> docOpt = documentacioClientRepository.findById(existent.getDni());
        DocumentacioClient doc = docOpt.orElse(new DocumentacioClient());
        doc.setDni(existent.getDni());

        try {
            if (!imatgeDniFile.isEmpty()) {
                doc.setImatgeDni(new Binary[] { new Binary(imatgeDniFile.getBytes()) });
            }
            if (!imatgeLlicenciaFile.isEmpty()) {
                doc.setImatgeLlicencia(new Binary[] { new Binary(imatgeLlicenciaFile.getBytes()) });
            }
        } catch (IOException e) {
            log.error("Error al processar les imatges del client amb DNI {}: {}", client.getDni(), e.getMessage());
            model.addAttribute("error", "Error al carregar les imatges.");
            return "client-modificar";
        }

        try {
            clientService.modificarClient(client);
            documentacioClientRepository.save(doc);
            log.info("El client amb DNI {} s'ha modificat correctament.", client.getDni());

        } catch (Exception e) {
            log.error("Error al desar el client amb DNI {}: {}", client.getDni(), e.getMessage());
            model.addAttribute("error", e.getMessage());
            model.addAttribute("client", client);
            return "client-modificar";
        }
        log.info("Caducitat llicència rebuda: {}", client.getLlicCaducitat());
        log.info("Caducitat DNI rebut: {}", client.getDniCaducitat());
        return "redirect:/clients/llistar";
    }

    /**
     * Llista els clients que tenen comptes pendents d'activació.
     *
     * @param model   Model per passar dades a la vista.
     * @param success Missatge d'èxit opcional per indicar que un compte s'ha
     *                activat.
     * @return El nom de la vista per validar comptes.
     */
    @GetMapping("/validar")
    public String llistarUsuarisPendents(Model model,
            @RequestParam(value = "success", required = false) String success) {

        log.info("S'estan llistant els clients pendents d'activació.");
        // Listar usuarios inactivos
        model.addAttribute("client", clientService.listarClientsInactius());
        if (success != null) {
            model.addAttribute("success", "El compte de l'usuari s'ha activat correctament.");
        }
        return "client-validar";
    }

    /**
     * Activa el compte d'un client pel seu DNI.
     *
     * @param dni                El DNI del client a activar.
     * @param redirectAttributes Atributs per passar missatges de redirecció.
     * @return Redirecció a la vista de validació de comptes.
     */
    @PostMapping("/activar/{dni}")
    public String activarUsuari(@PathVariable String dni, RedirectAttributes redirectAttributes) {
        log.info("S'està activant el compte del client amb DNI: {}", dni);
        clientService.activarClient(dni);
        redirectAttributes.addAttribute("success", "true");
        log.info("El compte del client amb DNI {} s'ha activat correctament.", dni);
        return "redirect:/clients/validar";
    }

    /**
     * Consulta els detalls d'un client.
     *
     * @param dni                El DNI del client a consultar.
     * @param model              Model per passar dades a la vista.
     * @param redirectAttributes Atributs per passar missatges de redirecció.
     * @return El nom de la vista per consultar els detalls del client.
     */
    @GetMapping("/consulta/{dni}")
    public String consultaClient(@PathVariable("dni") String dni, Model model, RedirectAttributes redirectAttributes) {

        log.info("S'estan consultant els detalls del client amb DNI: {}", dni);
        Optional<Client> client = clientService.findByDni(dni);

        if (client.isEmpty()) {
            log.error("No s'ha trobat cap client amb el DNI: {}", dni);
            redirectAttributes.addAttribute("error", "No s'ha trobat el client");
            return "redirect:/clients/llistar";
        }

        Client existent = client.get();

        // Cosas de imagenes
        Optional<DocumentacioClient> docOpt = documentacioClientRepository.findById(dni);

        String imatgeDniBase64 = null;
        String imatgeLlicenciaBase64 = null;

        if (docOpt.isPresent()) {
            DocumentacioClient doc = docOpt.get();
            if (doc.getImatgeDni() != null && doc.getImatgeDni().length > 0) {
                imatgeDniBase64 = Base64.getEncoder().encodeToString(doc.getImatgeDni()[0].getData());
            }
            if (doc.getImatgeLlicencia() != null && doc.getImatgeLlicencia().length > 0) {
                imatgeLlicenciaBase64 = Base64.getEncoder().encodeToString(doc.getImatgeLlicencia()[0].getData());
            }
        }
        model.addAttribute("imatgeDni", imatgeDniBase64);
        model.addAttribute("imatgeLlicencia", imatgeLlicenciaBase64);
        model.addAttribute("client", existent);
        log.info("S'han carregat els detalls del client amb DNI: {}", dni);
        return "client-consulta"; // Nom del fitxer HTML
    }

}
