/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author Anna
 */
@Controller
@RequestMapping("/registre")
public class RegistreController {

    private final ClientService clientService;

    @Autowired
    public RegistreController(ClientService clientService) {
        this.clientService = clientService;

    }

    //Carrega la pantalla HTML.
    @GetMapping("/alta-client")
    public String mostrarFormulari(Model model) {
        return "alta-client";  
    }

    @PostMapping("/alta-client")
    public ResponseEntity<Client> registrarClient(@ModelAttribute("client") Client client, 
                                  @RequestParam("confirmacioContrasenya") String confirmacioContrasenya, 
                                  Model model) throws Exception {
       
        if (!client.getContrasenya().equals(confirmacioContrasenya)){
            throw new IllegalArgumentException("La contrasenya no coincideix");
        }
        
        Client nouClient = clientService.altaClient(client);
        return new ResponseEntity<>(nouClient, HttpStatus.CREATED);
        
        

    }

}
