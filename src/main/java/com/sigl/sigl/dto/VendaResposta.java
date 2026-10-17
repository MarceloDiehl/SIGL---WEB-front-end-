package com.sigl.sigl.dto;

import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.model.ItemVenda;
import com.sigl.sigl.model.StatusVenda;
import com.sigl.sigl.model.Venda;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/** Venda como a API devolve: cabeçalho, nomes já resolvidos e itens. */
public record VendaResposta(
        Integer id,
        LocalDateTime dataHora,
        Integer idVendedor,
        String vendedor,
        Integer idMetodoPagamento,
        String metodoPagamento,
        String cpfNota,
        int quantidadeDeUnidades,
        BigDecimal valorTotal,
        BigDecimal valorRecebido,
        BigDecimal troco,
        StatusVenda status,
        LocalDateTime canceladaEm,
        List<Item> itens
) {

    public record Item(String codProduto, String tituloProduto, BigDecimal valorUnitario,
                       Integer quantidade, BigDecimal subtotal) {

        static Item de(ItemVenda item) {
            return new Item(item.getCodProduto(), item.getTituloProduto(), item.getValorUnitario(),
                    item.getQuantidade(), item.getSubtotal());
        }
    }

    public static VendaResposta de(Venda venda) {
        Funcionario vendedor = venda.getVendedor();
        return new VendaResposta(
                venda.getId(), venda.getDataHora(),
                vendedor.getId(), vendedor.getNome() + " " + vendedor.getSobrenome(),
                venda.getMetodoPagamento().getId(), venda.getMetodoPagamento().getDescricao(),
                venda.getCpfNota(), venda.getQuantidadeDeUnidades(),
                venda.getValorTotal(), venda.getValorRecebido(), venda.getTroco(),
                venda.getStatus(), venda.getCanceladaEm(),
                venda.getItens().stream().map(Item::de).toList()
        );
    }
}
