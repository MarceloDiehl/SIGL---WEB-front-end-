package com.sigl.sigl.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

/**
 * O que o caixa envia para registrar uma venda. Preços, títulos, total, troco e data/hora
 * não vêm do navegador: são calculados no servidor a partir do banco.
 */
public record NovaVendaPedido(
        @NotNull(message = "Informe o vendedor.")
        Integer idVendedor,

        @NotNull(message = "Informe a forma de pagamento.")
        Integer idMetodoPagamento,

        String cpfNota,

        // só para pagamento em dinheiro
        BigDecimal valorRecebido,

        @NotEmpty(message = "A venda precisa de pelo menos um item.")
        List<@Valid @NotNull Item> itens
) {

    public record Item(
            @NotBlank(message = "Informe o código do produto.")
            String codProduto,

            @NotNull(message = "Informe a quantidade.")
            @Positive(message = "A quantidade deve ser maior que zero.")
            Integer quantidade
    ) {
    }
}
