/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.repository.MySQL.ReservaRepository;
import Projecte2.DriftoCar.service.MySQL.ClientService;

import java.util.List;
import java.util.Optional;

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
        model.addAttribute("client", client);
        model.addAttribute("modeVisualitzar", false);

        log.info("Caducitat llicència al model: {}", client.getLlicCaducitat());
        log.info("Caducitat DNI al model: {}", client.getDniCaducitat());
        return "client-modificar";
    }

    // TODO añadir lista de nacionalidades de agente a cliente
    @PostMapping("/modificar")
    public String guardarClientModificat(@ModelAttribute("client") Client client) {
        Client existent = clientService.obtenirClientPerDni(client.getDni());
        if (client.getNacionalitat() == null || client.getNacionalitat().isEmpty()) {
            client.setNacionalitat(existent.getNacionalitat());
        }
        if (client.getContrasenya() == null || client.getContrasenya().isEmpty()) {
            client.setContrasenya(existent.getContrasenya());
        } else {
            // Si se ha proporcionado una nueva contraseña, encriptarla
            String contrasenyaEncriptada = passwordEncoder.encode(client.getContrasenya());
            client.setContrasenya(contrasenyaEncriptada);
        }
        clientService.modificarClient(client);
        log.info("Caducitat llicència rebuda: {}", client.getLlicCaducitat());
        log.info("Caducitat DNI rebut: {}", client.getDniCaducitat());
        return "redirect:/clients/llistar";
    }

    /*@GetMapping("/consulta/{dni}")
    public String visualitzarClient(@PathVariable String dni, Model model) {

        Client client = clientService.obtenirClientPerDni(dni);
        model.addAttribute("client", client);
        model.addAttribute("modeVisualitzar", true);
        return "client-modificar";
    }*/

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
        
        model.addAttribute("client", existent );
        return "client-consulta"; // Nom del fitxer HTML
    }

}
