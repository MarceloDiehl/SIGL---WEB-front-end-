package com.sigl.sigl.seguranca;

import com.sigl.sigl.controller.ViewController;
import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.model.apoio.Cargo;
import com.sigl.sigl.repository.FuncionarioRepository;
import com.sigl.sigl.service.HashSenha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** Regras de SegurancaConfig com o UsuarioService de verdade e o banco simulado. */
@WebMvcTest(ViewController.class)
@Import({SegurancaConfig.class, UsuarioService.class})
class SegurancaConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private FuncionarioRepository funcionarioRepository;

    private Funcionario funcionario;

    @BeforeEach
    void cadastrarFuncionario() {
        funcionario = new Funcionario();
        funcionario.setId(3);
        funcionario.setNome("Marcelo");
        funcionario.setSobrenome("Diehl");
        funcionario.setIdCargo(Cargo.VENDEDOR);
        funcionario.setEmail("marcelo@orion.com.br");
        funcionario.setSenha(HashSenha.md5("senha-antiga"));   // como está no banco antes da migração
        when(funcionarioRepository.findByEmail("marcelo@orion.com.br")).thenReturn(Optional.of(funcionario));
        when(funcionarioRepository.findById(3)).thenReturn(Optional.of(funcionario));
    }

    @Test
    @DisplayName("Tela sem login redireciona para /login (navegador pede HTML)")
    void paginaSemLogin_RedirecionaParaLogin() throws Exception {
        mockMvc.perform(get("/").accept(MediaType.TEXT_HTML))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    @DisplayName("API e telas carregadas por AJAX respondem 401 sem login (o script.js leva ao login)")
    void ajaxSemLogin_Devolve401() throws Exception {
        mockMvc.perform(get("/api/vendas")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/venda").header("X-Requested-With", "XMLHttpRequest")).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Tela de login é pública e o formulário leva o token CSRF")
    void telaDeLogin_PublicaComCsrf() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("name=\"_csrf\"")));
    }

    @Test
    @DisplayName("Login com senha antiga (MD5) funciona e regrava a senha em BCrypt")
    void loginComMd5_EntraEConverteParaBcrypt() throws Exception {
        mockMvc.perform(formLogin("/login").user("login-email", "marcelo@orion.com.br").password("login-senha", "senha-antiga"))
                .andExpect(authenticated().withUsername("marcelo@orion.com.br").withRoles("VENDEDOR"))
                .andExpect(redirectedUrl("/"));

        assertTrue(funcionario.getSenha().startsWith("{bcrypt}"), "senha deveria ter sido regravada em BCrypt");
        assertTrue(passwordEncoder.matches("senha-antiga", funcionario.getSenha()));
    }

    @Test
    @DisplayName("Senha errada não entra e volta para /login?erro")
    void loginComSenhaErrada_Recusa() throws Exception {
        mockMvc.perform(formLogin("/login").user("login-email", "marcelo@orion.com.br").password("login-senha", "chute"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?erro"));
    }

    @Test
    @DisplayName("Logout é POST com CSRF e volta para /login?saiu")
    void logout_VoltaParaLogin() throws Exception {
        mockMvc.perform(post("/logout").with(user(UsuarioLogado.de(funcionario))).with(csrf()))
                .andExpect(redirectedUrl("/login?saiu"))
                .andExpect(unauthenticated());
    }

    @Test
    @DisplayName("Logado, a página inicial mostra o nome e o cargo no menu")
    void inicioLogado_MostraUsuario() throws Exception {
        mockMvc.perform(get("/").with(user(UsuarioLogado.de(funcionario))))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Marcelo Diehl")))
                .andExpect(content().string(containsString("name=\"_csrf\" content=")));
    }
}
