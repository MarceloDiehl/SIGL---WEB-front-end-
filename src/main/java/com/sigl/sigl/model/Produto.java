package com.sigl.sigl.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cadastroproduto")
public class Produto {

    @Id
    @Column(name = "codProduto", unique = true, nullable = false, length = 6)
    @Size(min = 6, max = 6, message = "O código do produto deve ter 6 caracteres")
    private String codProduto;

    // Chave primária da tabela (gerada pelo banco); no JPA o identificador é o codProduto.
    // Só leitura: usada como referência em itemvenda.idProduto.
    @Column(name = "id", insertable = false, updatable = false)
    private Integer id;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 70, message = "O título não pode exceder 70 caracteres")
    @Column(nullable = false, length = 70)
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 255, message = "A descrição não pode exceder 255 caracteres")
    @Column(nullable = false)
    private String descricao;

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor unitário deve ser maior que zero")
    @Column(nullable = false, precision = 9, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    @Column(nullable = false)
    private Integer quantidade;

    @NotNull(message = "O tipo do produto é obrigatório")
    @Min(value = 1, message = "O ID do tipo deve ser positivo")
    @Column(name = "idTipo", nullable = false)
    private Integer idTipo;

    @NotNull(message = "A condição do produto é obrigatória")
    @Min(value = 1, message = "O ID da condição deve ser positivo")
    @Column(name = "idCondicao", nullable = false)
    private Integer idCondicao;

    @NotNull(message = "A data de entrada é obrigatória")
    @PastOrPresent(message = "A data de entrada não pode ser futura")
    @Column(nullable = false)
    private LocalDate entrada;

    // Construtores
    public Produto() {}

    public Produto(String codProduto, String titulo, String descricao, BigDecimal valor, Integer quantidade, Integer idTipo, Integer idCondicao, LocalDate entrada) {
        this.codProduto = codProduto;
        this.titulo = titulo;
        this.descricao = descricao;
        this.valor = valor;
        this.quantidade = quantidade;
        this.idTipo = idTipo;
        this.idCondicao = idCondicao;
        this.entrada = entrada;
    }

    // Getters e Setters
    public Integer getId() { return id; }

    public String getCodProduto() { return codProduto; }
    public void setCodProduto(String codProduto) { this.codProduto = codProduto; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public Integer getQuantidade() { return quantidade; }
    public void setQuantidade(Integer quantidade) { this.quantidade = quantidade; }

    public Integer getIdTipo() { return idTipo; }
    public void setIdTipo(Integer idTipo) { this.idTipo = idTipo; }

    public Integer getIdCondicao() { return idCondicao; }
    public void setIdCondicao(Integer idCondicao) { this.idCondicao = idCondicao; }

    public LocalDate getEntrada() { return entrada; }
    public void setEntrada(LocalDate entrada) { this.entrada = entrada; }

    // Métodos para remover quantidade vendida:
    public void removerQuantidade(int qtdVendida) {
        if (this.quantidade < qtdVendida) {
            throw new IllegalArgumentException("Quantidade em estoque insuficiente para o produto " + this.codProduto);
        }
        this.quantidade -= qtdVendida;
    }

    // Devolução ao estoque (cancelamento de venda)
    public void adicionarQuantidade(int qtdDevolvida) {
        this.quantidade += qtdDevolvida;
    }
}