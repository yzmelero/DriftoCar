/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Projecte2.DriftoCar.controller;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 * @author Anna
 */
@Controller
@RequestMapping("/usuaris")
public class ClientsController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/llistar")
    public String listarReservas(Model model) {
        List<Client> clients = clientService.llistarClients();
        model.addAttribute("clients", clients);
        return "client-llistar";
    }
}
