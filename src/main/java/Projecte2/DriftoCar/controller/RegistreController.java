/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MongoDB.DocumentacioClient;
import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.repository.MongoDB.DocumentacioClientRepository;
import Projecte2.DriftoCar.service.MySQL.ClientService;

import java.time.LocalDate;

import org.bson.types.Binary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador per gestionar el registre de nous clients.
 * 
 * Aquesta classe s'encarrega de:
 * - Mostrar el formulari d'alta de clients.
 * - Validar les dades introduïdes pel client.
 * - Registrar un nou client al sistema, incloent la seva documentació
 * associada.
 * 
 * Dependències:
 * - {@code ClientService}: Per gestionar la lògica de negoci del registre de
 * clients.
 * - {@code DocumentacioClientRepository}: Per emmagatzemar les imatges de
 * documentació del client.
 * 
 */
@Controller
@RequestMapping("/registre")
@Scope("session")
public class RegistreController {

    private static final Logger log = LoggerFactory.getLogger(RegistreController.class);

    @Autowired
    private ClientService clientService;
    @Autowired
    private DocumentacioClientRepository documentacioClientRepository;

    /**
     * Mostra el formulari d'alta de clients.
     * 
     * Si el model no conté un atribut {@code client}, se n'afegeix un de nou.
     * 
     * @param model Model utilitzat per passar dades a la vista.
     * @return Nom de la vista del formulari d'alta de clients.
     */
    @GetMapping("/client-alta")
    public String mostrarFormulari(Model model) {
        log.info("Accedint al formulari d'alta de clients.");
        if (!model.containsAttribute("client")) {
            model.addAttribute("client", new Client());
        }
        return "client-alta";
    }

    /**
     * Registra un nou client al sistema.
     * 
     * Aquest mètode valida les dades del client, incloent la data d'expiració del
     * DNI
     * i la llicència, la confirmació de la contrasenya i guarda la documentació
     * associada.
     * 
     * @param client                 Objecte que conté les dades del client.
     * @param imatgeDni              Imatge del DNI del client.
     * @param imatgeLlicencia        Imatge de la llicència de conduir del client.
     * @param confirmacioContrasenya Confirmació de la contrasenya.
     * @param model                  Model per passar dades a la vista.
     * @return Redirecció a la vista de llista de clients si té èxit, o torna al
     *         formulari en cas d'error.
     * @throws Exception Si hi ha un error durant el procés.
     */
    @PostMapping("/client-alta")
    public String registrarClient(@ModelAttribute("client") Client client,
            @RequestParam("imatgeDni") MultipartFile imatgeDni,
            @RequestParam("imatgeLlicencia") MultipartFile imatgeLlicencia,
            @RequestParam("confirmacioContrasenya") String confirmacioContrasenya,
            Model model) throws Exception {

        LocalDate currentDate = LocalDate.now();
        LocalDate maxDate = currentDate.plusYears(50);

        log.info("Inici del procés d'alta per al client amb DNI: {}", client.getDni());

        if (client.getDniCaducitat().isBefore(currentDate) || client.getDniCaducitat().isAfter(maxDate)) {
            log.warn("La data d'expiració del DNI no és vàlida: {}", client.getDniCaducitat());
            model.addAttribute("error", "La data d'expiració del DNI no és vàlida.");
            model.addAttribute("client", client);
            return "client-alta"; // Nombre de tu vista HTML
        }

        if (client.getLlicCaducitat().isBefore(currentDate) || client.getLlicCaducitat().isAfter(maxDate)) {
            log.warn("La data d'expiració de la llicència no és vàlida: {}", client.getLlicCaducitat());
            model.addAttribute("error", "La data d'expiració de la llicència no és vàlida.");
            model.addAttribute("client", client);
            return "client-alta";
        }
        if (!client.getContrasenya().equals(confirmacioContrasenya)) {
            log.error("La contrasenya no coincideix.");
            model.addAttribute("error", "La contrasenya no coincideix.");
            model.addAttribute("client", client);
            return "client-alta";
        }
        try {
            clientService.altaClient(client);
            log.info("Client registrat correctament amb DNI: {}", client.getDni());

            DocumentacioClient documentacio = new DocumentacioClient();
            documentacio.setDni(client.getDni());
            documentacio.setImatgeDni(new Binary[] { new Binary(imatgeDni.getBytes()) });
            documentacio.setImatgeLlicencia(new Binary[] { new Binary(imatgeLlicencia.getBytes()) });
            documentacioClientRepository.save(documentacio);
            log.info("Documentació del client guardada correctament per al DNI: {}", client.getDni());
        } catch (Exception e) {
            log.error("Error durant el registre del client amb DNI: {}", client.getDni(), e);
            model.addAttribute("error", e.getMessage());
            model.addAttribute("client", client);
            return "client-alta";
        }
        log.info("Procés d'alta finalitzat amb èxit per al client amb DNI: {}", client.getDni());
        return "redirect:/clients/llistar";

    }

}
