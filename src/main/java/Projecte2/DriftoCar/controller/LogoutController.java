package Projecte2.DriftoCar.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Controller
@RequestMapping("/logout") // URL personalizada para logout
public class LogoutController {

    @GetMapping
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Obtiene la autenticación actual
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null) {
            // Maneja la sesión y limpia el contexto de seguridad
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }

        // Redirige al usuario a la página de login con mensaje
        return "redirect:/login?logout=true";
    }
}
    

