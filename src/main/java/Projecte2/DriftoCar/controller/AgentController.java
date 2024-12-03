package Projecte2.DriftoCar.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import Projecte2.DriftoCar.entity.MySQL.Agent;
import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.service.MySQL.AgentService;
import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/agent")
public class AgentController {

    @Autowired
    private AgentService agentService;
    @Autowired
    private LocalitzacioService localitzacioService;

    @GetMapping("/llistar")
    public String llistarAgents(Model model) {
        List<Agent> agents = agentService.llistarAgents();
        model.addAttribute("agents", agents);
        return "agent-llistar"; // Nombre del archivo HTML
    }

    @GetMapping("/alta")
    public String altaAgente(Model model) {
        Agent agent = new Agent();
        agent.setLocalitzacio(new Localitzacio()); // Inicializar localitzacio
        model.addAttribute("agent", agent);

        // Obtener localitzacions desde la BBDD
        List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
        model.addAttribute("localitzacions", localitzacions);

        return "agent-alta"; // Ajusta según el nombre de tu template
    }

    @PostMapping("/guardar")
    public String guardarAgente(@Valid Agent agent, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "agent-alta"; // Retorna al formulario si hay errores de validación
        }

        try {
            agentService.altaAgent(agent); // Llama al servicio para guardar el agente
        } catch (RuntimeException e) { // Maneja el caso del DNI duplicado
            model.addAttribute("error", "Ja existeix un agent amb aquest DNI.");
            agent.setDni(""); // Limpia el campo DNI
            model.addAttribute("agent", agent); // Mantiene los demás datos
            List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
            model.addAttribute("localitzacions", localitzacions);
            return "agent-alta";
        }

        return "redirect:/agent/llistar"; // Redirige al listado si todo es correcto
    }

    /**
     * Carga el formulario de edición de un agente.
     *
     * @param dni   El DNI del agente a editar.
     * @param model El modelo para pasar los datos a la vista.
     * @return La vista del formulario de edición.
     */
    @GetMapping("/modificar/{dni}")
    public String modificarClients(@PathVariable("dni") String dni, Model model) {

        Agent agent = agentService.obtenirAgentPerDni(dni);
        if (agent == null) {
            throw new RuntimeException("No existeix cap agent amb aquest DNI.");

        }
        model.addAttribute("agent", agent);
        return "agent-modificar";
    }

    @PostMapping("/modificar")
    public String guardarClientModificat(@ModelAttribute("agent") Agent agent) {

        agentService.modificarAgent(agent);
        return "redirect:/agent/llistar";
    }
}
