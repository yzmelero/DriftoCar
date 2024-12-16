package Projecte2.DriftoCar.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.service.MySQL.ClientService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class LoginController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/verificar")
    public String postMethodName(@RequestParam("usuari") String usuari,
            @RequestParam("contrasenya") String contrasenya) {

        Optional<Client> user = clientService.findByUsuari(usuari);
        if (user.isEmpty() || !user.get().getContrasenya().equals(contrasenya)) {
            return "redirect:/login?error=true";
        }
                return "redirect:/";
    }

}
