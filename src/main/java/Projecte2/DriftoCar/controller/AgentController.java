package Projecte2.DriftoCar.controller;

import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.slf4j.LoggerFactory;
import org.bson.types.Binary;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import Projecte2.DriftoCar.entity.MongoDB.DocumentacioClient;
import Projecte2.DriftoCar.entity.MySQL.Agent;
import Projecte2.DriftoCar.entity.MySQL.Localitzacio;
import Projecte2.DriftoCar.repository.MongoDB.DocumentacioClientRepository;
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
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private DocumentacioClientRepository documentacioClientRepository;

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
            List<Localitzacio> localitzacions = agentService.getLocalitzacionsDisponibles();
            model.addAttribute("localitzacions", localitzacions);
        }
        return "agent-alta";
    }

    // TODO si hay un error en el dni, el telefono se pierde
    /**
     * Guarda un nuevo agente en la base de datos.
     * 
     * @param agent           El agente a guardar.
     * @param bindingResult   El resultado de la validación.
     * @param model           El modelo para pasar los datos a la vista.
     * @param imatgeDni       La imagen del DNI.
     * @param imatgeLlicencia La imagen de la licencia.
     * @return La vista de la lista de agentes si todo es correcto, o el formulario
     *         de alta si hay errores.
     */
    @PostMapping("/guardar")
    public String guardarAgente(@Valid Agent agent, BindingResult bindingResult, Model model,
            @RequestParam(value = "imatgeDni", required = false) MultipartFile imatgeDni,
            @RequestParam(value = "imatgeLlicencia", required = false) MultipartFile imatgeLlicencia) {

        if (bindingResult.hasErrors()) {
            List<Localitzacio> localitzacions = localitzacioService.llistarLocalitzacions();
            model.addAttribute("localitzacions", localitzacions);
            model.addAttribute("agent", agent);
            return "agent-alta";
        }
        String contrasenya = agent.getContrasenya();
        try {
            agentService.altaAgent(agent);
            DocumentacioClient documentacio = new DocumentacioClient();
            documentacio.setDni(agent.getDni());

            if (imatgeDni != null && !imatgeDni.isEmpty()) {
                documentacio.setImatgeDni(new Binary[] { new Binary(imatgeDni.getBytes()) });
            }
            if (imatgeLlicencia != null && !imatgeLlicencia.isEmpty()) {
                documentacio.setImatgeLlicencia(new Binary[] { new Binary(imatgeLlicencia.getBytes()) });
            }
            documentacioClientRepository.save(documentacio);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            agent.setContrasenya(contrasenya);
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
    public String modificarAgent(@PathVariable("dni") String dni, Model model) {

        Agent agent = agentService.obtenirAgentPerDni(dni);
        if (agent == null) {
            throw new RuntimeException("No existeix cap agent amb aquest DNI.");

        }
        Optional<DocumentacioClient> docOpt = documentacioClientRepository.findById(dni);
        String imatgeDniBase64 = null;
        String imatgeLlicenciaBase64 = null;

        if (docOpt.isPresent()) {
            DocumentacioClient doc = docOpt.get();
            if (doc.getImatgeDni() != null && doc.getImatgeDni().length > 0) {
                imatgeDniBase64 = Base64.getEncoder().encodeToString(doc.getImatgeDni()[0].getData());
            }
            if (doc.getImatgeLlicencia() != null && doc.getImatgeLlicencia().length > 0) {
                imatgeLlicenciaBase64 = Base64.getEncoder().encodeToString(doc.getImatgeLlicencia()[0].getData());
            }
        }

        model.addAttribute("agent", agent);
        model.addAttribute("imatgeDni", imatgeDniBase64);
        model.addAttribute("imatgeLlicencia", imatgeLlicenciaBase64);

        return "agent-modificar";
    }

    @PostMapping("/modificar")
    public String guardarAgentModificat(@Valid Agent agent,
            @RequestParam("imatgeDni") MultipartFile imatgeDniFile,
            @RequestParam("imatgeLlicencia") MultipartFile imatgeLlicenciaFile,
            Model model) {
        Agent existent = agentService.obtenirAgentPerDni(agent.getDni());
        agent.setRol(existent.getRol());
        if (agent.getNacionalitat() == null || agent.getNacionalitat().isEmpty()) {
            agent.setNacionalitat(existent.getNacionalitat());
        }
        if (agent.getContrasenya() == null || agent.getContrasenya().isEmpty()) {
            agent.setContrasenya(existent.getContrasenya());
        } else {
            // Si se ha proporcionado una nueva contraseña, encriptarla
            String contrasenyaEncriptada = passwordEncoder.encode(agent.getContrasenya());
            agent.setContrasenya(contrasenyaEncriptada);
        }

        try {
            agentService.modificarAgent(agent);

            // Recuperar o crear documentación en MongoDB
            DocumentacioClient documentacio = documentacioClientRepository.findById(agent.getDni())
                    .orElse(new DocumentacioClient());
            documentacio.setDni(agent.getDni());

            if (!imatgeDniFile.isEmpty()) {
                documentacio.setImatgeDni(new Binary[] { new Binary(imatgeDniFile.getBytes()) });
            }
            if (!imatgeLlicenciaFile.isEmpty()) {
                documentacio.setImatgeLlicencia(new Binary[] { new Binary(imatgeLlicenciaFile.getBytes()) });
            }
            documentacioClientRepository.save(documentacio);
        } catch (Exception e) {
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
        Optional<DocumentacioClient> docOpt = documentacioClientRepository.findById(dni);
        String imatgeDniBase64 = null;
        String imatgeLlicenciaBase64 = null;

        if (docOpt.isPresent()) {
            DocumentacioClient doc = docOpt.get();
            if (doc.getImatgeDni() != null && doc.getImatgeDni().length > 0) {
                imatgeDniBase64 = Base64.getEncoder().encodeToString(doc.getImatgeDni()[0].getData());
            }
            if (doc.getImatgeLlicencia() != null && doc.getImatgeLlicencia().length > 0) {
                imatgeLlicenciaBase64 = Base64.getEncoder().encodeToString(doc.getImatgeLlicencia()[0].getData());
            }
        }

        model.addAttribute("imatgeDni", imatgeDniBase64);
        model.addAttribute("imatgeLlicencia", imatgeLlicenciaBase64);
        model.addAttribute("agent", agent);
        return "agent-consulta"; // Nom de la plantilla
    }
}
