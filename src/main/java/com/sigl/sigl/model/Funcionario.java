package com.sigl.sigl.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "cadastroFuncionario")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 45)
    private String nome;

    @Column(nullable = false, length = 45)
    private String sobrenome;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(name = "idCargo", nullable = false)
    private Integer idCargo;

    @Column(name = "idEscolaridade", nullable = false)
    private Integer idEscolaridade;

    @Column(length = 45)
    private String especialidade;

    @Column(length = 15)
    private String telefone;

    @Column(name = "CPF", unique = true, nullable = false, length = 14)
    private String cpf;

    @Column(name = "idGenero", nullable = false)
    private Integer idGenero;

    @Column(nullable = false, length = 45)
    private String endereco;

    @Column(nullable = false)
    private Integer numeroPredial;

    @Column(nullable = false, length = 45)
    private String bairro;

    @Column(nullable = false, length = 45)
    private String cidade;

    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    @Column(name = "CEP", length = 9)
    private String cep;

    @Column(name = "contatoEmergencia", nullable = false, length = 15)
    private String contatoEmergencia;

    @Column(name = "dataEntrada", nullable = false)
    private LocalDate dataEntrada;

    @Column(name = "RG", nullable = false, length = 14)
    private String rg; // RG não é único no banco

    @Column(name = "CTPAS", nullable = false, length = 20)
    private String ctpas;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String senha;
}
