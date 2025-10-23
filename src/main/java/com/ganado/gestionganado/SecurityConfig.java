package com.ganado.gestionganado;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // permitir recursos estáticos y la página de login
                .requestMatchers("/css/**", "/js/**", "/img/**", "/favicon.ico", "/login", "/error").permitAll()
                // todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")                // ruta GET que mostrará el login
                .defaultSuccessUrl("/ganado", true) // a dónde ir al entrar correctamente
                .permitAll()                        // permite usar el endpoint de login
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails abuelo = User.withUsername("abuelo")
                .password("{noop}1234") // para pruebas; luego usa bcrypt
                .roles("ADMIN")
                .build();

        UserDetails nieto = User.withUsername("nieto")
                .password("{noop}5678")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(abuelo, nieto);
    }
}
