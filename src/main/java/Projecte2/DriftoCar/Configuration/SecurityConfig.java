package Projecte2.DriftoCar.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

        @Autowired
        private ValidadorUsuaris validadorUsuaris;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(auth -> auth
                                                .requestMatchers("/registre/client-alta").permitAll()
                                                .requestMatchers("/login").permitAll()
                                                .requestMatchers("/").hasAnyRole("CLIENT", "ADMIN", "AGENT")

                                                // Matchers d'agent sin post
                                                .requestMatchers("/agent/alta").hasRole("ADMIN")
                                                .requestMatchers("/agent/consulta/**").hasRole("ADMIN")
                                                .requestMatchers("/agent/llistar").hasAnyRole("ADMIN")
                                                .requestMatchers("/agent/modificar/**").hasRole("ADMIN")
                                                .requestMatchers("/agent/esborrar/**").hasRole("ADMIN")

                                                // Matchers de client sin post
                                                .requestMatchers("/clients/llistar").hasRole("ADMIN")
                                                .requestMatchers("/clients/esborrar/**").hasRole("ADMIN")
                                                .requestMatchers("/clients/modificar/**").hasRole("ADMIN")
                                                .requestMatchers("/clients/consulta/**").hasRole("ADMIN")
                                                .requestMatchers("/clients/validar").hasRole("ADMIN")
                                                .requestMatchers("/clients/activar/**").hasRole("ADMIN")

                                                // Matchers de incidencia sin post
                                                .requestMatchers("/incidencia/obrir/**").hasAnyRole("ADMIN", "AGENT")
                                                .requestMatchers("/incidencia/llistar-incidencies")
                                                .hasAnyRole("ADMIN", "AGENT")
                                                .requestMatchers("/incidencia/llistar").hasAnyRole("ADMIN", "AGENT")
                                                .requestMatchers("/incidencia/tancar/**").hasAnyRole("ADMIN", "AGENT")
                                                .requestMatchers("/incidencia/detall/**").hasAnyRole("ADMIN", "AGENT")
                                                .requestMatchers("/incidencia/descargar-pdf/**")
                                                .hasAnyRole("ADMIN", "AGENT")
                                                .requestMatchers("/incidencia/historial")
                                                .hasAnyRole("ADMIN", "AGENT")

                                                // Matchers de localitzacio sin post
                                                .requestMatchers("/localitzacio/llistar").hasRole("ADMIN")
                                                .requestMatchers("/localitzacio/alta").hasRole("ADMIN")
                                                .requestMatchers("/localitzacio/esborrar/**").hasRole("ADMIN")
                                                .requestMatchers("/localitzacio/modificar/**").hasRole("ADMIN")
                                                .requestMatchers("/localitzacio/consulta/**").hasRole("ADMIN")

                                                // Matchers de reserva sin post
                                                .requestMatchers("/reserva/llistar")
                                                .hasAnyRole("ADMIN", "CLIENT", "AGENT")
                                                .requestMatchers("/reserva/alta").hasAnyRole("ADMIN", "CLIENT", "AGENT")
                                                .requestMatchers("/reserva/consulta/**")
                                                .hasAnyRole("ADMIN", "CLIENT", "AGENT")
                                                .requestMatchers("/reserva/llistar")
                                                .hasAnyRole("ADMIN", "CLIENT", "AGENT")
                                                .requestMatchers("/reserva/lliurar/**").hasAnyRole("ADMIN", "AGENT")
                                                .requestMatchers("/reserva/anular/**")
                                                .hasAnyRole("ADMIN", "AGENT", "CLIENT")
                                                .requestMatchers("/reserva/retornar/**").hasAnyRole("ADMIN", "AGENT")
                                                .requestMatchers("/reserva/retornar/calculPreu/**").hasAnyRole("ADMIN", "AGENT")

                                                // Matchers de vehicle sin post
                                                .requestMatchers("/vehicle/llistar")
                                                .hasAnyRole("ADMIN", "CLIENT", "AGENT")
                                                .requestMatchers("/vehicle/afegir").hasRole("ADMIN")
                                                .requestMatchers("/vehicle/esborrar/**").hasRole("ADMIN")
                                                .requestMatchers("/vehicle/modificar/**").hasRole("ADMIN")
                                                .requestMatchers("/vehicle/activar/**").hasAnyRole("ADMIN", "AGENT")
                                                .requestMatchers("/vehicle/consulta/**")
                                                .hasAnyRole("ADMIN", "CLIENT", "AGENT")
                                                .requestMatchers("/vehicle/desactivar/**").hasAnyRole("ADMIN", "AGENT")

                                                .anyRequest().authenticated()) // El resto requiere autenticación

                                .formLogin(login -> login
                                                .loginPage("/login") // Página de login personalizada
                                                .defaultSuccessUrl("/") // Redirigir a "/" tras login exitoso
                                                .failureUrl("/login?error=true") // Redirigir a login con error si
                                                                                 // fallan las credenciales
                                                .usernameParameter("usuari") // Nombre del campo de usuario
                                                .passwordParameter("contrasenya") // Nombre del campo de contraseña
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutUrl("/logout")
                                                .logoutSuccessUrl("/login?logout=true") // Redirigir a login tras logout
                                                .invalidateHttpSession(true) // Invalida la sesión HTTP
                                                .deleteCookies("JSESSIONID") // Elimina las cookies de sesión
                                                .permitAll())
                                .exceptionHandling(handling -> handling
                                                .accessDeniedPage("/errorPermisos"));
                ;

                return http.build();
        }

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
                // return NoOpPasswordEncoder.getInstance(); // Codificación de contraseñas
        }

        @SuppressWarnings("removal")
        @Bean
        public AuthenticationManager authManager(HttpSecurity http) throws Exception {
                return http.getSharedObject(AuthenticationManagerBuilder.class)
                                .userDetailsService(validadorUsuaris)
                                .passwordEncoder(passwordEncoder())
                                .and()
                                .build();
        }
}
