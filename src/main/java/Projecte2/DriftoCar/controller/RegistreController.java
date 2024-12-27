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
 *
 * @author Anna
 */
@Controller
@RequestMapping("/registre")
@Scope("session")
public class RegistreController {

    @Autowired
    private ClientService clientService;
    @Autowired
    private DocumentacioClientRepository documentacioClientRepository;

    // Carrega la pantalla HTML.
    @GetMapping("/client-alta")
    public String mostrarFormulari(Model model) {
        if (!model.containsAttribute("client")) {
            model.addAttribute("client", new Client());
        }
        return "client-alta";
    }

    @PostMapping("/client-alta")
    public String registrarClient(@ModelAttribute("client") Client client,
            @RequestParam("imatgeDni") MultipartFile imatgeDni,
            @RequestParam("imatgeLlicencia") MultipartFile imatgeLlicencia,
            @RequestParam("confirmacioContrasenya") String confirmacioContrasenya,
            Model model) throws Exception {

        LocalDate currentDate = LocalDate.now();
        LocalDate maxDate = currentDate.plusYears(50);

        if (client.getDniCaducitat().isBefore(currentDate) || client.getDniCaducitat().isAfter(maxDate)) {
            model.addAttribute("error", "La data d'expiració del DNI no és vàlida.");
            model.addAttribute("client", client);
            return "client-alta"; // Nombre de tu vista HTML
        }

        if (client.getLlicCaducitat().isBefore(currentDate) || client.getLlicCaducitat().isAfter(maxDate)) {
            model.addAttribute("error", "La data d'expiració de la llicència no és vàlida.");
            model.addAttribute("client", client);
            return "client-alta";
        }
        if (!client.getContrasenya().equals(confirmacioContrasenya)) {
            throw new IllegalArgumentException("La contrasenya no coincideix");
        }
        try {
            clientService.altaClient(client);
            DocumentacioClient documentacio = new DocumentacioClient();
            documentacio.setDni(client.getDni());
            documentacio.setImatgeDni(new Binary[] { new Binary(imatgeDni.getBytes()) });
            documentacio.setImatgeLlicencia(new Binary[] { new Binary(imatgeLlicencia.getBytes()) });
            documentacioClientRepository.save(documentacio);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("client", client);
            return "client-alta";
        }

        return "redirect:/clients/llistar";

    }

}
