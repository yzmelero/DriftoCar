/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
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
@Scope("session")
public class RegistreController {

    @Autowired
    private ClientService clientService;

    //Carrega la pantalla HTML.
    @GetMapping("/client-alta")
    public String mostrarFormulari(Model model) {
        return "client-alta";  
    }

    @PostMapping("/client-alta")
    public String registrarClient(@ModelAttribute("client") Client client, 
                                  @RequestParam("confirmacioContrasenya") String confirmacioContrasenya, 
                                  Model model) throws Exception {
       
        if (!client.getContrasenya().equals(confirmacioContrasenya)){
            throw new IllegalArgumentException("La contrasenya no coincideix");
        }
        
        clientService.altaClient(client);
        
        return "redirect:/clients/llistar";
                        
        

    }

}
