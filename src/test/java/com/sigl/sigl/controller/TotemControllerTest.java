package com.sigl.sigl.controller;

import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.model.apoio.Cargo;
import com.sigl.sigl.repository.FuncionarioRepository;
import com.sigl.sigl.seguranca.SegurancaConfig;
import com.sigl.sigl.seguranca.UsuarioLogado;
import com.sigl.sigl.service.HashSenha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TotemController.class)
@Import(SegurancaConfig.class)   // PasswordEncoder real (BCrypt + MD5 legado) e as regras de acesso
class TotemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private FuncionarioRepository funcionarioRepository;

    private MockHttpSession sessao;

    // quem ativou o totem: vendedor logado, senha já em BCrypt
    private static final UsuarioLogado VENDEDOR = new UsuarioLogado(3, "marcelo@orion.com.br", null,
            "Marcelo", "Diehl", Cargo.VENDEDOR, "Vendedor");

    private static Funcionario funcionario(int id, String hashDaSenha) {
        Funcionario f = new Funcionario();
        f.setId(id);
        f.setSenha(hashDaSenha);
        return f;
    }

    @BeforeEach
    void ativarTotem() throws Exception {
        sessao = new MockHttpSession();
        when(funcionarioRepository.findById(3)).thenReturn(Optional.of(funcionario(3, passwordEncoder.encode("senha-do-vendedor"))));
        // gerente que ainda não entrou desde a migração: senha no formato MD5 antigo
        when(funcionarioRepository.findByIdCargo(Cargo.GERENTE)).thenReturn(List.of(funcionario(7, HashSenha.md5("senha-do-gerente"))));

        mockMvc.perform(post("/api/totem/ativar").session(sessao).with(user(VENDEDOR)).with(csrf()))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    private void encerrarCom(String senha, boolean encerrado) throws Exception {
        mockMvc.perform(post("/api/totem/encerrar").session(sessao).with(user(VENDEDOR)).with(csrf())
                        .contentType(MediaType.APPLICATION_JSON).content("{\"senha\":\"" + senha + "\"}"))
                .andExpect(jsonPath("$.encerrado").value(encerrado));
    }

    private void statusDeveSer(boolean ativo) throws Exception {
        mockMvc.perform(get("/api/totem/status").session(sessao).with(user(VENDEDOR)))
                .andExpect(jsonPath("$.ativo").value(ativo));
    }

    @Test
    @DisplayName("Depois de ativar, o status da sessão é ativo")
    void ativar_MarcaSessaoComoAtiva() throws Exception {
        statusDeveSer(true);
    }

    @Test
    @DisplayName("Senha vazia não encerra o totem")
    void encerrar_SemSenha_ContinuaAtivo() throws Exception {
        encerrarCom("", false);
        statusDeveSer(true);
    }

    @Test
    @DisplayName("Senha que não é de quem ativou nem de um gerente não encerra")
    void encerrar_SenhaErrada_ContinuaAtivo() throws Exception {
        encerrarCom("chute", false);
        statusDeveSer(true);
    }

    @Test
    @DisplayName("A senha de quem ativou (BCrypt) encerra o totem")
    void encerrar_SenhaDoUsuario_Encerra() throws Exception {
        encerrarCom("senha-do-vendedor", true);
        statusDeveSer(false);
    }

    @Test
    @DisplayName("A senha de um gerente (ainda em MD5) também encerra")
    void encerrar_SenhaDeGerente_Encerra() throws Exception {
        encerrarCom("senha-do-gerente", true);
        statusDeveSer(false);
    }

    @Test
    @DisplayName("Sem login, a API do totem responde 401")
    void semLogin_Devolve401() throws Exception {
        mockMvc.perform(get("/api/totem/status")).andExpect(status().isUnauthorized());
    }
}
