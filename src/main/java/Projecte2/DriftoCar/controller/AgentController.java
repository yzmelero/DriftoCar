package Projecte2.DriftoCar.controller;

import java.util.List;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import Projecte2.DriftoCar.entity.MySQL.Agent;
import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.service.MySQL.AgentService;
import Projecte2.DriftoCar.service.MySQL.LocalitzacioService;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/agent")
@Scope("session")
public class AgentController {

    Logger log = LoggerFactory.getLogger(AgentController.class);
    @Autowired
    private AgentService agentService;
    @Autowired
    private LocalitzacioService localitzacioService;


    /**
     * Filtra els agents pel seu DNI.
     *
     * @param dni   El DNI per filtrar. Si no s'especifica, es mostren tots els
     *              agents.
     * @param model El model per passar dades a la vista.
     * @return El nom de la plantilla Thymeleaf per al llistat d'agents.
     */
    @GetMapping("/llistar")
    public String llistarAgents(@RequestParam(value = "dni", required = false) String dni, Model model) {
        
        
        List<Agent> agents;

        if (dni != null && !dni.isEmpty()) {
            agents = agentService.filtrarPerDni(dni); // Cerca agents pel DNI
        } else {
            agents = agentService.llistarAgents(); // Mostra tots els agents
        }
        model.addAttribute("filtroDni", dni);
        model.addAttribute("agents", agents);
        return "agent-llistar"; // Nombre del archivo HTML
    }

    @GetMapping("/alta")
    public String altaAgente(Model model) {

        if (!model.containsAttribute("agent")) {
            model.addAttribute("agent", new Agent());
            List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
            model.addAttribute("localitzacions", localitzacions);
        }
        return "agent-alta";
    }

    @PostMapping("/guardar")
    public String guardarAgente(@Valid Agent agent, BindingResult bindingResult, Model model) {
        
        if (bindingResult.hasErrors()) {
            List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
            model.addAttribute("localitzacions", localitzacions);
            model.addAttribute("agent", agent);
            return "agent-alta";
        }

        try {
            agentService.altaAgent(agent);
        } catch (RuntimeException e) {
            String error = e.getMessage();

            if (error.contains("DNI")) {
                agent.setDni(""); // Limpia el campo DNI si hay error de duplicidad
            }

            model.addAttribute("error", error);
            model.addAttribute("agent", agent); // Mantiene los demás datos en el formulario

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
    public String guardarClientModificat(@Valid Agent agent, Model model) {
        try {
            agentService.modificarAgent(agent);
        } catch (RuntimeException e) {
            String error = e.getMessage();
            model.addAttribute("error", error);
            model.addAttribute("agent", agent);
            return "agent-modificar";
        }

        return "redirect:/agent/llistar";
    }

    @GetMapping("/esborrar/{dni}")
    public String eliminarAgent(@PathVariable("dni") String dni, Model model, Agent agent) {
        log.info("S'ha entrat al mètode esborrarController.");
        try {
            agentService.eliminarAgent(agent); // Llama al servicio para eliminar
        } catch (RuntimeException e) {
            model.addAttribute("error", "No s'ha pogut eliminar l'agent.");
            return "agent-llistar"; // Vuelve a la lista con un mensaje de error
        }

        return "redirect:/agent/llistar"; // Redirige al listado después de eliminar
    }

    /**
     * Consulta d'un agent.
     *
     * @param dni   El DNI de l'agent.
     * @param model El model per passar les dades a la vista.
     * @return El nom de la plantilla Thymeleaf per a la pàgina de detalls.
     */
    @GetMapping("/consulta/{dni}")
    public String mostrarDetallAgent(@PathVariable("dni") String dni, Model model) {
        Agent agent = agentService.obtenirAgentPerDni(dni); // Busca l'agent pel seu DNI
        if (agent == null) {
            throw new RuntimeException("No s'ha trobat cap agent amb el DNI especificat.");
        }
        model.addAttribute("agent", agent);
        return "agent-consulta"; // Nom de la plantilla
    }
}
