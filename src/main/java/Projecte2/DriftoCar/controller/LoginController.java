package Projecte2.DriftoCar.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.service.MySQL.ClientService;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controlador per gestionar el procés de login dels usuaris.
 * 
 * Aquesta classe proporciona funcionalitats per:
 * - Mostrar el formulari de login amb missatges d'error o confirmació de
 * tancament de sessió.
 * - Verificar les credencials de l'usuari per permetre l'accés al sistema.
 * 
 * Dependències:
 * - {@code ClientService}: Servei per gestionar les operacions relacionades amb
 * els clients, com la validació d'usuaris.
 * 
 */
@Controller
@Scope("session")
public class LoginController {

    @Autowired
    private ClientService clientService;

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    /**
     * Mostra el formulari de login amb possibles missatges d'error o tancament de
     * sessió.
     * 
     * @param error  Missatge d'error si les credencials no són vàlides.
     * @param logout Missatge de confirmació quan l'usuari tanca la sessió
     *               correctament.
     * @param model  Model per passar dades a la vista.
     * @return El nom de la vista del formulari de login.
     */
    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {
        if (error != null) {
            log.warn("Error d'autenticació: Usuari o contrasenya incorrectes.");
            model.addAttribute("error", "Usuari o contrasenya incorrectes, torna a intentar");
        }
        if (logout != null) {
            log.info("Sessió tancada correctament.");
            model.addAttribute("logout", "Sessió tancada correctament");
        }
        return "login";
    }

    /**
     * Verifica les credencials introduïdes per l'usuari.
     * 
     * @param usuari      Nom d'usuari introduït al formulari.
     * @param contrasenya Contrasenya introduïda al formulari.
     * @param model       Model per passar dades a la vista.
     * @return Redirecció a la pàgina principal si les credencials són vàlides, o al formulari de login si són incorrectes.
     */
    @PostMapping("/verificar")
    public String postMethodName(@RequestParam("usuari") String usuari,
            @RequestParam("contrasenya") String contrasenya,
            Model model) {

        log.info("Iniciant verificació de credencials per a l'usuari: {}", usuari);
        Optional<Client> user = clientService.findByUsuari(usuari);
        if (user.isEmpty() || !user.get().getContrasenya().equals(contrasenya)) {
            log.warn("Error d'autenticació per a l'usuari: {}", usuari);
            String error = "Usuari o contrasenya incorrectes, torna a intentar";
            model.addAttribute("error", error);
            return "/login";

        }
        log.info("Usuari autenticat correctament: {}", usuari);
        return "redirect:/";
    }

}
