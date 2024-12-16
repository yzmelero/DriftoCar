package Projecte2.DriftoCar.Configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Autowired
    private ValidadorUsuaris validadorUsuaris;

    @SuppressWarnings("removal")
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/client/alta").permitAll()
                        .requestMatchers("/login").permitAll()
                        .requestMatchers("/agent/llistar").hasAnyRole("ADMIN", "AGENT")
                        .requestMatchers("/agent/alta").hasRole("ADMIN")
                        .requestMatchers("/client/llistar").hasAnyRole("CLIENT", "ADMIN", "AGENT")
                        .requestMatchers("/").hasAnyRole("CLIENT", "ADMIN", "AGENT")
                        .anyRequest().authenticated()) // El resto requiere autenticación
                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("usuari") // Página personalizada de login
                        .passwordParameter("contrasenya")
                        .defaultSuccessUrl("/"))
                .logout(logout -> logout
                        .permitAll());

        return http.build();
    }


    //TODO añadir cifrado
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance(); // Codificación de contraseñas
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
