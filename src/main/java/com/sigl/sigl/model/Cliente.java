package com.sigl.sigl.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "cadastrocliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    private String nome;

    @Column(nullable = false, length = 45)
    private String sobrenome;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "CPF", nullable = false, length = 14, unique = true)
    private String cpf;

    @Column(name = "RG", nullable = false, length = 14)
    private String rg; // RG não é único no banco de dados

    @Column(nullable = false, length = 45)
    private String endereco;

    @Column(nullable = false)
    private Integer numeroPredial;

    @Column(nullable = false, length = 45)
    private String bairro;

    @Column(nullable = false, length = 45)
    private String cidade;

    @Column(nullable = false, length = 2)
    private String estado;

    @Column(name = "CEP", length = 9)
    private String cep;

    @Column(length = 15)
    private String telefone;

    @Column(nullable = false)
    private LocalDate clienteDesde;
}
