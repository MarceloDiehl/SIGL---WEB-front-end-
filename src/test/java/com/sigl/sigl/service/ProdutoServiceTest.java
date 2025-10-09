package com.sigl.sigl.service;

import com.sigl.sigl.model.Produto;
import com.sigl.sigl.repository.ProdutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    @DisplayName("Deve cadastrar produto com sucesso quando código não existe")
    void cadastrar_DeveSalvarProduto_QuandoCodigoNaoExiste() {
        Produto produto = new Produto("PRD001", "Notebook", "Desc", BigDecimal.TEN, 10, 1, 1, LocalDate.now());
        when(produtoRepository.existsById("PRD001")).thenReturn(false);
        when(produtoRepository.save(produto)).thenReturn(produto);

        Produto produtoSalvo = produtoService.cadastrar(produto);

        assertNotNull(produtoSalvo);
        assertEquals("PRD001", produtoSalvo.getCodProduto());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar cadastrar produto com código que já existe")
    void cadastrar_DeveLancarExcecao_QuandoCodigoJaExiste() {
        Produto produto = new Produto("PRD001", "Notebook", "Desc", BigDecimal.TEN, 10, 1, 1, LocalDate.now());
        when(produtoRepository.existsById("PRD001")).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            produtoService.cadastrar(produto);
        });

        assertEquals("Erro: Já existe um produto com o código PRD001", exception.getMessage());
        verify(produtoRepository, never()).save(any(Produto.class));
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar remover quantidade maior que o estoque")
    void removerQuantidade_DeveLancarExcecao_QuandoEstoqueInsuficiente() {
        Produto produtoEmEstoque = new Produto("PRD001", "Mouse", "Desc", BigDecimal.ONE, 5, 1, 1, LocalDate.now());
        when(produtoRepository.findByCodProduto("PRD001")).thenReturn(Optional.of(produtoEmEstoque));

        assertThrows(IllegalArgumentException.class, () -> {
            produtoService.removerQuantidade("PRD001", 10);
        });

        verify(produtoRepository, never()).save(any(Produto.class));
    }
}