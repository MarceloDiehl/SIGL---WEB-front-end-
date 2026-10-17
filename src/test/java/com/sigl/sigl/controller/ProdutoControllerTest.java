package com.sigl.sigl.controller;

import com.sigl.sigl.model.Produto;
import com.sigl.sigl.repository.ItemVendaRepository;
import com.sigl.sigl.repository.ProdutoRepository;
import com.sigl.sigl.service.ProdutoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProdutoController.class)
@WithMockUser   // a API exige login; aqui basta um usuário qualquer autenticado
class ProdutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProdutoRepository produtoRepository;

    @MockitoBean
    private ProdutoService produtoService;

    @MockitoBean
    private ItemVendaRepository itemVendaRepository;

    private Produto produto;

    @BeforeEach
    void preparar() {
        produto = new Produto("132437", "Abbey Road", "LP", new BigDecimal("150.00"), 10, 1, 1, LocalDate.now());
        ReflectionTestUtils.setField(produto, "id", 1);
        when(produtoRepository.findByCodProduto("132437")).thenReturn(Optional.of(produto));
    }

    @Test
    @DisplayName("Produto já vendido não é excluído (409)")
    void excluir_ProdutoVendido_Devolve409() throws Exception {
        when(itemVendaRepository.existsByIdProduto(1)).thenReturn(true);

        mockMvc.perform(delete("/api/produtos/132437").with(csrf())).andExpect(status().isConflict());
        verify(produtoRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Produto nunca vendido pode ser excluído")
    void excluir_ProdutoNuncaVendido_Exclui() throws Exception {
        when(itemVendaRepository.existsByIdProduto(1)).thenReturn(false);

        mockMvc.perform(delete("/api/produtos/132437").with(csrf())).andExpect(status().isOk());
        verify(produtoRepository).delete(produto);
    }
}
