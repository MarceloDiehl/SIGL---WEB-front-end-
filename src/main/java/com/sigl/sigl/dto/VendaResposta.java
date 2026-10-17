package com.sigl.sigl.dto;

import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.model.ItemVenda;
import com.sigl.sigl.model.StatusVenda;
import com.sigl.sigl.model.Venda;
import com.sigl.sigl.seguranca.DadosPessoais;

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

    /** cpfCompleto: só o gerente vê o CPF da nota inteiro; para os demais cargos vai mascarado. */
    public static VendaResposta de(Venda venda, boolean cpfCompleto) {
        Funcionario vendedor = venda.getVendedor();
        return new VendaResposta(
                venda.getId(), venda.getDataHora(),
                vendedor.getId(), vendedor.getNome() + " " + vendedor.getSobrenome(),
                venda.getMetodoPagamento().getId(), venda.getMetodoPagamento().getDescricao(),
                cpfCompleto ? venda.getCpfNota() : DadosPessoais.mascararCpf(venda.getCpfNota()),
                venda.getQuantidadeDeUnidades(),
                venda.getValorTotal(), venda.getValorRecebido(), venda.getTroco(),
                venda.getStatus(), venda.getCanceladaEm(),
                venda.getItens().stream().map(Item::de).toList()
        );
    }
}
