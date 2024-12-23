package Projecte2.DriftoCar.Configuration;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import Projecte2.DriftoCar.entity.MySQL.Agent;
import Projecte2.DriftoCar.entity.MySQL.Client;
import Projecte2.DriftoCar.repository.MySQL.AgentRepository;
import Projecte2.DriftoCar.repository.MySQL.ClientRepository;

@Service
public class ValidadorUsuaris implements UserDetailsService {

    @Autowired
    private ClientRepository clientRepository;
    
    @Autowired
    private AgentRepository agentRepository;

   /* @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return clientRepository.findByUsuari(username).get();
    }*/
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Buscar en la tabla de agentes
        Optional<Agent> agent = agentRepository.findByUsuari(username);
        if (agent.isPresent()) {
            Agent agentExistent = agent.get();
            return User.builder()
                    .username(agentExistent.getUsuari())
                    .password(agentExistent.getContrasenya()) // La contraseña debe estar codificada
                    .roles(agentExistent.getRol().toString()) // ADMIN o AGENT
                    .build();
        }
        // Buscar en la tabla de clientes
        Optional<Client> client = clientRepository.findByUsuari(username);
        if (client.isPresent()) {
            Client clientExistent = client.get();
            if (!clientExistent.isActivo()) {
                throw new UsernameNotFoundException("El compte encara no ha estat validat per un administrador.");
            }
            return User.builder()
                    .username(clientExistent.getUsuari())
                    .password(clientExistent.getContrasenya()) // La contraseña debe estar codificada
                    .roles("CLIENT") // Rol de cliente
                    .build();
        }

        

        throw new UsernameNotFoundException("Usuari no trobat: " + username);
    }
}
