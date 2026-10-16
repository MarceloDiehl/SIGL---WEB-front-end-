package com.sigl.sigl.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cadastrovendas")

public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "codProduto", nullable = false, length = 6)
    private String codProduto;

    @Column(name = "tituloProduto", nullable = false, length = 70)
    private String tituloProduto;

    @Column(name = "valorUnitario", nullable = false, precision = 9, scale = 2)
    private BigDecimal valorUnitario;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "valorTotal", nullable = false, precision = 9, scale = 2)
    private BigDecimal valorTotal;

    @Column(nullable = false)
    private LocalDate dataVenda;

    @Column(name = "idVendedor", nullable = false)
    private Integer idVendedor;

    @Column(name = "cpfNota", length = 14)
    private String cpfNota;

    @Column(name = "idMetodoPagamento", nullable = false)
    private Integer idMetodoPagamento;
    //Construtores:

    public Venda() {
    }

    public Venda(Integer id, String codProduto, String tituloProduto, BigDecimal valorUnitario, Integer quantidade, BigDecimal valorTotal, LocalDate dataVenda, Integer idVendedor, String cpfNota, Integer idMetodoPagamento) {
        this.id = id;
        this.codProduto = codProduto;
        this.tituloProduto = tituloProduto;
        this.valorUnitario = valorUnitario;
        this.quantidade = quantidade;
        this.valorTotal = valorTotal;
        this.dataVenda = dataVenda;
        this.idVendedor = idVendedor;
        this.cpfNota = cpfNota;
        this.idMetodoPagamento = idMetodoPagamento;
    }
    //Getters e setters:

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCodProduto() {
        return codProduto;
    }

    public void setCodProduto(String codProduto) {
        this.codProduto = codProduto;
    }

    public String getTituloProduto() {
        return tituloProduto;
    }

    public void setTituloProduto(String tituloProduto) {
        this.tituloProduto = tituloProduto;
    }

    public BigDecimal getValorUnitario() {
        return valorUnitario;
    }

    public void setValorUnitario(BigDecimal valorUnitario) {
        this.valorUnitario = valorUnitario;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public BigDecimal getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(BigDecimal valorTotal) {
        this.valorTotal = valorTotal;
    }

    public LocalDate getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDate dataVenda) {
        this.dataVenda = dataVenda;
    }

    public Integer getIdVendedor() {
        return idVendedor;
    }

    public void setIdVendedor(Integer idVendedor) {
        this.idVendedor = idVendedor;
    }

    public String getCpfNota() {
        return cpfNota;
    }

    public void setCpfNota(String cpfNota) {
        this.cpfNota = cpfNota;
    }

    public Integer getIdMetodoPagamento() {
        return idMetodoPagamento;
    }

    public void setIdMetodoPagamento(Integer idMetodoPagamento) {
        this.idMetodoPagamento = idMetodoPagamento;
    }
}
