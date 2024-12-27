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
 *
 * @author Anna
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

    @GetMapping("/llistar")
    public String llistarClients(@RequestParam(value = "searchCognoms", required = false) String searchCognoms,
            @RequestParam(value = "searchNacionalitat", required = false) String searchNacionalitat,
            @RequestParam(value = "searchTelefon", required = false) String searchTelefon,
            @RequestParam(value = "searchEmail", required = false) String searchEmail,
            Model model) {

        List<Client> clients;

        if ((searchCognoms != null && !searchCognoms.isEmpty())
                || (searchNacionalitat != null && !searchNacionalitat.isEmpty())
                || (searchTelefon != null && !searchTelefon.isEmpty())
                || (searchEmail != null && !searchEmail.isEmpty())) {
            clients = clientService.cercarClients(searchCognoms, searchNacionalitat, searchTelefon, searchEmail);
        } else {
            clients = clientService.llistarClients();
        }

        model.addAttribute("clients", clients);
        model.addAttribute("searchCognoms", searchCognoms);
        model.addAttribute("searchNacionalitat", searchNacionalitat);
        model.addAttribute("searchTelefon", searchTelefon);
        model.addAttribute("searchEmail", searchEmail);

        return "client-llistar";

    }

    @GetMapping("/esborrar/{dni}")
    public String esborrarClients(@PathVariable("dni") String dni, Client client,
            RedirectAttributes redirectAttributes) {
        try {
            clientService.baixaClient(client);
            redirectAttributes.addFlashAttribute("success", "El client s'ha esborrat correctament.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/clients/llistar";

    }

    @GetMapping("/modificar/{dni}")
    public String modificarClients(@PathVariable("dni") String dni, Model model) {

        Client client = clientService.obtenirClientPerDni(dni);
        if (client == null) {
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
        return "client-modificar";
    }

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
            model.addAttribute("error", "La data d'expiració del DNI no és vàlida.");
            model.addAttribute("client", client);
            return "client-modificar";
        }

        if (client.getLlicCaducitat().isBefore(currentDate) || client.getLlicCaducitat().isAfter(maxDate)) {
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
            model.addAttribute("error", "Error al carregar les imatges.");
            return "client-modificar";
        }

        try {
            clientService.modificarClient(client);
            documentacioClientRepository.save(doc);

        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("client", client);
            return "client-modificar";
        }
        log.info("Caducitat llicència rebuda: {}", client.getLlicCaducitat());
        log.info("Caducitat DNI rebut: {}", client.getDniCaducitat());
        return "redirect:/clients/llistar";
    }

    @GetMapping("/validar")
    public String llistarUsuarisPendents(Model model,
            @RequestParam(value = "success", required = false) String success) {
        // Listar usuarios inactivos
        model.addAttribute("client", clientService.listarClientsInactius());
        if (success != null) {
            model.addAttribute("success", "El compte de l'usuari s'ha activat correctament.");
        }
        return "client-validar";
    }

    @PostMapping("/activar/{dni}")
    public String activarUsuari(@PathVariable String dni, RedirectAttributes redirectAttributes) {
        clientService.activarClient(dni);
        redirectAttributes.addAttribute("success", "true");
        return "redirect:/clients/validar";
    }

    @GetMapping("/consulta/{dni}")
    public String consultaClient(@PathVariable("dni") String dni, Model model, RedirectAttributes redirectAttributes) {

        Optional<Client> client = clientService.findByDni(dni);

        if (client.isEmpty()) {
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
        return "client-consulta"; // Nom del fitxer HTML
    }

}
