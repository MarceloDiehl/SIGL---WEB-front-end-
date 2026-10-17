package com.sigl.sigl.controller;

import com.sigl.sigl.dto.ApoioResposta;
import com.sigl.sigl.dto.ApoioResposta.Opcao;
import com.sigl.sigl.service.ApoioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApoioController.class)
class ApoioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApoioService apoioService;

    @Test
    @DisplayName("Devolve todas as listas de apoio em uma resposta, com cache privado")
    void listar_DevolveListasDeApoio() throws Exception {
        List<Opcao> vazia = List.of();
        when(apoioService.listarTudo()).thenReturn(new ApoioResposta(
                List.of(new Opcao(1, "Disco de Vinil"), new Opcao(2, "CD")),
                vazia, List.of(new Opcao(2, "Gerente")), vazia, vazia, List.of(new Opcao(1, "Dinheiro"))));

        mockMvc.perform(get("/api/apoio"))
                .andExpect(status().isOk())
                .andExpect(header().string("Cache-Control", "max-age=600, private"))
                .andExpect(jsonPath("$.tipos[1].id").value(2))
                .andExpect(jsonPath("$.tipos[1].descricao").value("CD"))
                .andExpect(jsonPath("$.cargos[0].descricao").value("Gerente"))
                .andExpect(jsonPath("$.metodosPagamento[0].descricao").value("Dinheiro"));
    }
}
