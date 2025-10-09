package com.sigl.sigl.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cadastroproduto")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Produto {

    @Id
    @Column(name = "codProduto", unique = true, nullable = false, length = 6)
    @Size(min = 6, max = 6, message = "O código do produto deve ter 6 caracteres")
    private String codProduto;

    @NotBlank(message = "O título é obrigatório")
    @Size(max = 70, message = "O título não pode exceder 70 caracteres")
    @Column(name = "titulo", nullable = false, length = 70)
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória")
    @Size(max = 255, message = "A descrição não pode exceder 255 caracteres")
    @Column(name = "descricao", nullable = false)
    private String descricao;

    @NotNull(message = "O valor é obrigatório")
    @DecimalMin(value = "0.01", message = "O valor unitário deve ser maior que zero")
    @Column(name = "valor", nullable = false, precision = 9, scale = 2)
    private BigDecimal valor;

    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 0, message = "A quantidade não pode ser negativa")
    @Column(name = "quantidade", nullable = false)
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
    @Column(name = "entrada", nullable = false)
    private LocalDate entrada;

    public void removerQuantidade(int qtdVendida) {
        if (this.quantidade < qtdVendida) {
            throw new IllegalArgumentException("Quantidade em estoque insuficiente para o produto " + this.codProduto);
        }
        this.quantidade -= qtdVendida;
    }
}