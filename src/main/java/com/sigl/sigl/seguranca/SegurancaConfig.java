package com.sigl.sigl.seguranca;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Map;

@Configuration
public class SegurancaConfig {

    /**
     * Senhas novas: BCrypt, gravadas como "{bcrypt}$2a$10$...". Senhas antigas (MD5, sem prefixo)
     * ainda são aceitas no login e, logo depois de um login certo, regravadas em BCrypt
     * (DelegatingPasswordEncoder.upgradeEncoding + UsuarioService.updatePassword).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        DelegatingPasswordEncoder encoder = new DelegatingPasswordEncoder("bcrypt",
                Map.of("bcrypt", new BCryptPasswordEncoder()));
        encoder.setDefaultPasswordEncoderForMatches(new Md5LegadoPasswordEncoder());
        return encoder;
    }

    @Bean
    public SecurityFilterChain filtroDeSeguranca(HttpSecurity http) throws Exception {
        // Chamadas AJAX (API e telas carregadas por .load()) recebem 401 em vez do HTML da tela de
        // login; o script.js trata o 401 levando para /login?expirou.
        RequestMatcher chamadaAjax = new OrRequestMatcher(
                PathPatternRequestMatcher.withDefaults().matcher("/api/**"),
                new RequestHeaderRequestMatcher("X-Requested-With", "XMLHttpRequest"));

        http
                .authorizeHttpRequests(RegrasDeAcesso::aplicar)   // matriz de permissões por cargo
                .formLogin(login -> login
                        .loginPage("/login")                  // tela própria (ViewController + login.html)
                        .loginProcessingUrl("/login")         // POST do formulário, tratado pelo Spring
                        .usernameParameter("login-email")
                        .passwordParameter("login-senha")
                        .defaultSuccessUrl("/", true)
                        .failureUrl("/login?erro")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")                 // só por POST, com o token CSRF
                        .logoutSuccessUrl("/login?saiu")
                        .permitAll())
                .exceptionHandling(erros -> erros
                        .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), chamadaAjax));
        // CSRF fica ligado (padrão): formulários Thymeleaf recebem o token sozinhos e o script.js
        // envia o token das <meta> no cabeçalho de toda chamada AJAX que altera dados.
        return http.build();
    }
}
