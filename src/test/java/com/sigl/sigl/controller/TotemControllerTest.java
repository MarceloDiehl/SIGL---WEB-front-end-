package com.sigl.sigl.controller;

import com.sigl.sigl.repository.FuncionarioRepository;
import com.sigl.sigl.service.HashSenha;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TotemController.class)
class TotemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FuncionarioRepository funcionarioRepository;

    private MockHttpSession sessao;

    @BeforeEach
    void ativarTotem() throws Exception {
        sessao = new MockHttpSession();
        when(funcionarioRepository.existsBySenha(anyString())).thenReturn(false);
        when(funcionarioRepository.existsBySenha(HashSenha.md5("senha-certa"))).thenReturn(true);

        mockMvc.perform(post("/api/totem/ativar").session(sessao))
                .andExpect(jsonPath("$.ativo").value(true));
    }

    private void encerrarCom(String corpo, boolean encerrado) throws Exception {
        mockMvc.perform(post("/api/totem/encerrar").session(sessao)
                        .contentType(MediaType.APPLICATION_JSON).content(corpo))
                .andExpect(jsonPath("$.encerrado").value(encerrado));
    }

    private void statusDeveSer(boolean ativo) throws Exception {
        mockMvc.perform(get("/api/totem/status").session(sessao))
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
        encerrarCom("{\"senha\":\"\"}", false);
        statusDeveSer(true);
    }

    @Test
    @DisplayName("Senha que não é de nenhum funcionário não encerra o totem")
    void encerrar_SenhaErrada_ContinuaAtivo() throws Exception {
        encerrarCom("{\"senha\":\"chute\"}", false);
        statusDeveSer(true);
    }

    @Test
    @DisplayName("Senha de um funcionário encerra o totem")
    void encerrar_SenhaDeFuncionario_Encerra() throws Exception {
        encerrarCom("{\"senha\":\"senha-certa\"}", true);
        statusDeveSer(false);
    }
}
