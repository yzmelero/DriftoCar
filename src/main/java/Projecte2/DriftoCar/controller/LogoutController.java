package Projecte2.DriftoCar.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador per gestionar el procés de logout dels usuaris.
 * 
 * Aquesta classe s'encarrega de:
 * - Finalitzar la sessió de l'usuari autenticat.
 * - Netejar el context de seguretat per assegurar que no hi hagi dades persistents.
 * - Redirigir l'usuari a la pàgina de login amb un missatge de confirmació de tancament de sessió.
 * 
 * Dependències:
 * - {@code HttpServletRequest} i {@code HttpServletResponse} per gestionar la petició i resposta HTTP.
 * - {@code SecurityContextHolder} per accedir al context de seguretat actual.
 * - {@code SecurityContextLogoutHandler} per realitzar el procés de logout.
 * 
 */
@Controller
@RequestMapping("/logout") // URL personalizada para logout
public class LogoutController {

    private static final Logger log = LoggerFactory.getLogger(LogoutController.class);

    /**
     * Gestiona el procés de logout de l'usuari.
     * 
     * Aquest mètode:
     * - Accedeix a l'autenticació actual.
     * - Finalitza la sessió i neteja el context de seguretat si l'usuari està autenticat.
     * - Redirigeix a la pàgina de login amb un missatge indicant que la sessió s'ha tancat.
     * 
     * @param request  Petició HTTP de l'usuari.
     * @param response Resposta HTTP per al navegador de l'usuari.
     * @return Redirecció a la pàgina de login amb un missatge de logout.
     */
    @GetMapping
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Obtiene la autenticación actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            // Maneja la sesión y limpia el contexto de seguridad
            log.info("Finalitzant la sessió per a l'usuari: {}", auth.getName());
            new SecurityContextLogoutHandler().logout(request, response, auth);
            log.info("Sessió finalitzada correctament per a l'usuari: {}", auth.getName());

        }

        // Redirige al usuario a la página de login con mensaje
        log.info("Redirigint a la pàgina de login amb el missatge de logout.");
        return "redirect:/login?logout=true";
    }
}
    

