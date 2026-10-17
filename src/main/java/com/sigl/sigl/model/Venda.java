package com.sigl.sigl.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Cabeçalho da venda: quem vendeu, quando, como foi pago e o total.
 * O total é sempre a soma dos itens, calculada aqui; não há setter para ele.
 */
@Entity
@Table(name = "venda")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idVendedor")
    private Funcionario vendedor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idMetodoPagamento")
    private MetodoPagamento metodoPagamento;

    @Column(length = 11)
    private String cpfNota;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorRecebido;

    @Column(precision = 10, scale = 2)
    private BigDecimal troco;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusVenda status = StatusVenda.CONCLUIDA;

    private LocalDateTime canceladaEm;

    @OneToMany(mappedBy = "venda", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("id")
    private List<ItemVenda> itens = new ArrayList<>();

    protected Venda() {
    }

    public Venda(Funcionario vendedor, MetodoPagamento metodoPagamento, String cpfNota, LocalDateTime dataHora) {
        this.vendedor = vendedor;
        this.metodoPagamento = metodoPagamento;
        this.cpfNota = cpfNota;
        this.dataHora = dataHora;
    }

    /** Registra o item com o preço atual do produto e soma o subtotal ao total da venda. */
    public void adicionarItem(Produto produto, int quantidade) {
        ItemVenda item = new ItemVenda(this, produto, quantidade);
        itens.add(item);
        valorTotal = valorTotal.add(item.getSubtotal());
    }

    /** Pagamento em dinheiro: guarda o valor recebido e calcula o troco. */
    public void registrarPagamentoEmDinheiro(BigDecimal recebido) {
        if (recebido == null) {
            throw new IllegalArgumentException("Informe o valor recebido em dinheiro.");
        }
        if (recebido.compareTo(valorTotal) < 0) {
            throw new IllegalArgumentException("O valor recebido é menor que o total da venda.");
        }
        this.valorRecebido = recebido;
        this.troco = recebido.subtract(valorTotal);
    }

    public void cancelar(LocalDateTime quando) {
        if (status == StatusVenda.CANCELADA) {
            throw new IllegalStateException("A venda nº " + id + " já está cancelada.");
        }
        this.status = StatusVenda.CANCELADA;
        this.canceladaEm = quando;
    }

    public int getQuantidadeDeUnidades() {
        return itens.stream().mapToInt(ItemVenda::getQuantidade).sum();
    }

    public Integer getId() {
        return id;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public Funcionario getVendedor() {
        return vendedor;
    }

    public MetodoPagamento getMetodoPagamento() {
        return metodoPagamento;
    }

    public String getCpfNota() {
        return cpfNota;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public BigDecimal getValorRecebido() {
        return valorRecebido;
    }

    public BigDecimal getTroco() {
        return troco;
    }

    public StatusVenda getStatus() {
        return status;
    }

    public LocalDateTime getCanceladaEm() {
        return canceladaEm;
    }

    public List<ItemVenda> getItens() {
        return Collections.unmodifiableList(itens);
    }
}
