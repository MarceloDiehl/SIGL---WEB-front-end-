package com.sigl.sigl.controller;

import com.sigl.sigl.dto.NovaVendaPedido;
import com.sigl.sigl.exception.RegraDeNegocioException;
import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.model.MetodoPagamento;
import com.sigl.sigl.model.Produto;
import com.sigl.sigl.model.Venda;
import com.sigl.sigl.service.VendaService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VendaController.class)
class VendaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VendaService vendaService;

    private static final String PEDIDO = """
            {"idVendedor": 3, "idMetodoPagamento": 4, "itens": [{"codProduto": "132437", "quantidade": 2}]}""";

    @Test
    @DisplayName("POST registra a venda: 201, Location e número da venda")
    void registrar_Devolve201ComLocation() throws Exception {
        Funcionario vendedor = new Funcionario();
        vendedor.setId(3);
        vendedor.setNome("Marcelo");
        vendedor.setSobrenome("Diehl");
        Venda venda = new Venda(vendedor, new MetodoPagamento(4, "PIX"), null, LocalDateTime.now());
        venda.adicionarItem(new Produto("132437", "Abbey Road", "LP", new BigDecimal("150.00"), 10, 1, 1, LocalDate.now()), 2);
        ReflectionTestUtils.setField(venda, "id", 42);
        when(vendaService.registrar(any(NovaVendaPedido.class))).thenReturn(venda);

        mockMvc.perform(post("/api/vendas").contentType(MediaType.APPLICATION_JSON).content(PEDIDO))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/vendas/42"))
                .andExpect(jsonPath("$.id").value(42))
                .andExpect(jsonPath("$.valorTotal").value(300.00))
                .andExpect(jsonPath("$.vendedor").value("Marcelo Diehl"))
                .andExpect(jsonPath("$.itens[0].subtotal").value(300.00));
    }

    @Test
    @DisplayName("POST sem itens é recusado antes de chegar ao serviço")
    void registrar_SemItens_Devolve400() throws Exception {
        mockMvc.perform(post("/api/vendas").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idVendedor\": 3, \"idMetodoPagamento\": 4, \"itens\": []}"))
                .andExpect(status().isBadRequest());
        verify(vendaService, never()).registrar(any());
    }

    @Test
    @DisplayName("Regra de negócio vira ProblemDetail com a mensagem em detail")
    void registrar_SemEstoque_DevolveProblemDetail() throws Exception {
        when(vendaService.registrar(any(NovaVendaPedido.class)))
                .thenThrow(new RegraDeNegocioException(HttpStatus.CONFLICT, "Só há 1 unidade de Abbey Road."));

        mockMvc.perform(post("/api/vendas").contentType(MediaType.APPLICATION_JSON).content(PEDIDO))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Só há 1 unidade de Abbey Road."));
    }
}
