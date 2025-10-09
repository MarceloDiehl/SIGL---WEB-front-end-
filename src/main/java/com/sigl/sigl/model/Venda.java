package com.sigl.sigl.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "cadastrovendas")
@Data
@NoArgsConstructor
@AllArgsConstructor
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
}
