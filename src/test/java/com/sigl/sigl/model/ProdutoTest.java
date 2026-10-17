package com.sigl.sigl.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProdutoTest {

    private final Produto produto = new Produto("PRD001", "Mouse", "Desc", BigDecimal.ONE, 5, 1, 1, LocalDate.now());

    @Test
    @DisplayName("Não remove mais do que há em estoque e mantém a quantidade")
    void removerQuantidade_EstoqueInsuficiente_LancaExcecao() {
        assertThrows(IllegalArgumentException.class, () -> produto.removerQuantidade(10));
        assertEquals(5, produto.getQuantidade());
    }

    @Test
    @DisplayName("Remover e devolver ajustam a quantidade em estoque")
    void removerEAdicionarQuantidade() {
        produto.removerQuantidade(3);
        assertEquals(2, produto.getQuantidade());
        produto.adicionarQuantidade(3);
        assertEquals(5, produto.getQuantidade());
    }
}
