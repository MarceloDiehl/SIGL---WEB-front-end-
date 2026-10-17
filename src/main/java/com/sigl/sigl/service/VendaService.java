package com.sigl.sigl.service;

import com.sigl.sigl.dto.NovaVendaPedido;
import com.sigl.sigl.exception.RegraDeNegocioException;
import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.model.ItemVenda;
import com.sigl.sigl.model.MetodoPagamento;
import com.sigl.sigl.model.Produto;
import com.sigl.sigl.model.Venda;
import com.sigl.sigl.repository.FuncionarioRepository;
import com.sigl.sigl.repository.MetodoPagamentoRepository;
import com.sigl.sigl.repository.ProdutoRepository;
import com.sigl.sigl.repository.VendaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final FuncionarioRepository funcionarioRepository;
    private final MetodoPagamentoRepository metodoPagamentoRepository;
    private final ValidatorService validatorService;

    public VendaService(VendaRepository vendaRepository, ProdutoRepository produtoRepository,
                        FuncionarioRepository funcionarioRepository,
                        MetodoPagamentoRepository metodoPagamentoRepository,
                        ValidatorService validatorService) {
        this.vendaRepository = vendaRepository;
        this.produtoRepository = produtoRepository;
        this.funcionarioRepository = funcionarioRepository;
        this.metodoPagamentoRepository = metodoPagamentoRepository;
        this.validatorService = validatorService;
    }

    /**
     * Registra a venda inteira numa transação: confere e baixa o estoque de cada item, calcula
     * o total com os preços do banco e grava cabeçalho e itens. Se algo falhar, nada é gravado.
     */
    @Transactional
    public Venda registrar(NovaVendaPedido pedido) {
        Funcionario vendedor = funcionarioRepository.findById(pedido.idVendedor())
                .orElseThrow(() -> recusar(HttpStatus.BAD_REQUEST, "Vendedor nº " + pedido.idVendedor() + " não encontrado."));
        MetodoPagamento metodo = metodoPagamentoRepository.findById(pedido.idMetodoPagamento())
                .orElseThrow(() -> recusar(HttpStatus.BAD_REQUEST, "Forma de pagamento inválida."));

        Venda venda = new Venda(vendedor, metodo, cpfDaNota(pedido.cpfNota()), LocalDateTime.now());

        // O mesmo código em duas linhas vira um item só. A ordem por código é sempre a mesma,
        // o que evita que dois caixas travem produtos em ordens opostas (deadlock).
        Map<String, Integer> quantidades = new TreeMap<>();
        for (NovaVendaPedido.Item item : pedido.itens()) {
            quantidades.merge(item.codProduto().trim(), item.quantidade(), Integer::sum);
        }

        quantidades.forEach((codigo, quantidade) -> {
            Produto produto = produtoRepository.buscarParaAtualizarEstoque(codigo)
                    .orElseThrow(() -> recusar(HttpStatus.BAD_REQUEST, "Produto " + codigo + " não encontrado."));
            if (produto.getQuantidade() < quantidade) {
                throw recusar(HttpStatus.CONFLICT, estoqueInsuficiente(produto));
            }
            produto.removerQuantidade(quantidade);
            venda.adicionarItem(produto, quantidade);
        });

        if (metodo.isDinheiro()) {
            try {
                venda.registrarPagamentoEmDinheiro(pedido.valorRecebido());
            } catch (IllegalArgumentException e) {
                throw recusar(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        }

        return vendaRepository.save(venda);
    }

    /** Cancela a venda e devolve ao estoque as quantidades de cada item. */
    @Transactional
    public Venda cancelar(Integer idVenda) {
        Venda venda = buscar(idVenda);
        try {
            venda.cancelar(LocalDateTime.now());
        } catch (IllegalStateException e) {
            throw recusar(HttpStatus.CONFLICT, e.getMessage());
        }
        venda.getItens().stream()
                .sorted(Comparator.comparing(ItemVenda::getCodProduto))
                .forEach(item -> produtoRepository.buscarParaAtualizarEstoque(item.getCodProduto())
                        .ifPresent(produto -> produto.adicionarQuantidade(item.getQuantidade())));
        return venda;
    }

    @Transactional(readOnly = true)
    public Venda buscar(Integer idVenda) {
        return vendaRepository.findComItensById(idVenda)
                .orElseThrow(() -> recusar(HttpStatus.NOT_FOUND, "Venda nº " + idVenda + " não encontrada."));
    }

    @Transactional(readOnly = true)
    public List<Venda> listar(String codigoProduto) {
        if (codigoProduto == null || codigoProduto.isBlank()) {
            return vendaRepository.findAllByOrderByDataHoraDescIdDesc();
        }
        return vendaRepository.buscarPorCodigoDeProduto(codigoProduto.trim());
    }

    // CPF da nota é opcional; quando vem, é validado e guardado só com os dígitos
    private String cpfDaNota(String cpf) {
        if (cpf == null || cpf.isBlank()) {
            return null;
        }
        try {
            validatorService.validarCpf(cpf);
        } catch (IllegalArgumentException e) {
            throw recusar(HttpStatus.BAD_REQUEST, "CPF da nota inválido.");
        }
        return cpf.replaceAll("\\D", "");
    }

    private static String estoqueInsuficiente(Produto produto) {
        int disponivel = produto.getQuantidade();
        if (disponivel == 0) {
            return produto.getTitulo() + " está sem estoque.";
        }
        return "Só há " + disponivel + (disponivel == 1 ? " unidade" : " unidades") + " de " + produto.getTitulo() + ".";
    }

    private static RegraDeNegocioException recusar(HttpStatus status, String mensagem) {
        return new RegraDeNegocioException(status, mensagem);
    }
}
