package com.sigl.sigl.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Item de uma venda. Código, título e preço são copiados do produto no momento da venda
 * ("fotografia"): mudanças posteriores no cadastro do produto não alteram vendas registradas.
 */
@Entity
@Table(name = "itemvenda")
public class ItemVenda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "idVenda")
    private Venda venda;

    @Column(nullable = false)
    private Integer idProduto;

    @Column(nullable = false, length = 6)
    private String codProduto;

    @Column(nullable = false, length = 70)
    private String tituloProduto;

    @Column(nullable = false, precision = 9, scale = 2)
    private BigDecimal valorUnitario;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    protected ItemVenda() {
    }

    ItemVenda(Venda venda, Produto produto, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade deve ser maior que zero.");
        }
        this.venda = venda;
        this.idProduto = produto.getId();
        this.codProduto = produto.getCodProduto();
        this.tituloProduto = produto.getTitulo();
        this.valorUnitario = produto.getValor();
        this.quantidade = quantidade;
        this.subtotal = valorUnitario.multiply(BigDecimal.valueOf(quantidade)).setScale(2, RoundingMode.HALF_EVEN);
    }

    public Integer getId() {
        return id;
    }

    public Integer getIdProduto() {
        return idProduto;
    }

    public String getCodProduto() {
        return codProduto;
    }

    public String getTituloProduto() {
        return tituloProduto;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
