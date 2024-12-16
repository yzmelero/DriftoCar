package Projecte2.DriftoCar.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class LoginController {

    @Autowired
    private ClientService clientService;

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Model model) {
        if (error != null) {
            model.addAttribute("error", "Usuari o contrasenya incorrectes, torna a intentar");
        }
        if (logout != null) {
            model.addAttribute("logout", "Sessió tancada correctament");
        }
        return "login";
    }

    @PostMapping("/verificar")
    public String postMethodName(@RequestParam("usuari") String usuari,
            @RequestParam("contrasenya") String contrasenya,
            Model model) {

        Optional<Client> user = clientService.findByUsuari(usuari);
        if (user.isEmpty() || !user.get().getContrasenya().equals(contrasenya)) {
            String error = "Usuari o contrasenya incorrectes, torna a intentar";
            model.addAttribute("error", error);
            return "/login";

        }
        return "redirect:/";
    }

}
