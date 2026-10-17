package com.sigl.sigl.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sigl.sigl.model.apoio.Cargo;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDate;

@Entity
@Table(name = "cadastroFuncionario")

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

    // Mesmo idCargo, como entidade, só para leitura (nome do cargo no login); grava-se pelo idCargo
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idCargo", insertable = false, updatable = false)
    private Cargo cargo;

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
    @JdbcTypeCode(SqlTypes.CHAR)   // UF: CHAR(2) no banco
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
    //Getter e setter:

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public void setSobrenome(String sobrenome) {
        this.sobrenome = sobrenome;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public Integer getIdCargo() {
        return idCargo;
    }

    public void setIdCargo(Integer idCargo) {
        this.idCargo = idCargo;
    }

    public Integer getIdEscolaridade() {
        return idEscolaridade;
    }

    public void setIdEscolaridade(Integer idEscolaridade) {
        this.idEscolaridade = idEscolaridade;
    }

    public String getEspecialidade() {
        return especialidade;
    }

    public void setEspecialidade(String especialidade) {
        this.especialidade = especialidade;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Integer getIdGenero() {
        return idGenero;
    }

    public void setIdGenero(Integer idGenero) {
        this.idGenero = idGenero;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public Integer getNumeroPredial() {
        return numeroPredial;
    }

    public void setNumeroPredial(Integer numeroPredial) {
        this.numeroPredial = numeroPredial;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getContatoEmergencia() {
        return contatoEmergencia;
    }

    public void setContatoEmergencia(String contatoEmergencia) {
        this.contatoEmergencia = contatoEmergencia;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getCtpas() {
        return ctpas;
    }

    public void setCtpas(String ctpas) {
        this.ctpas = ctpas;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public Cargo getCargo() {
        return cargo;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }
    //Construtor simples:

    public Funcionario() {
    }
    //Construtor completo:

    public Funcionario(Integer id, String nome, String sobrenome, LocalDate dataNascimento, Integer idCargo, Integer idEscolaridade, String especialidade, String telefone, String cpf, Integer idGenero, String endereco, Integer numeroPredial, String bairro, String cidade, String estado, String cep, String contatoEmergencia, LocalDate dataEntrada, String rg, String ctpas, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.dataNascimento = dataNascimento;
        this.idCargo = idCargo;
        this.idEscolaridade = idEscolaridade;
        this.especialidade = especialidade;
        this.telefone = telefone;
        this.cpf = cpf;
        this.idGenero = idGenero;
        this.endereco = endereco;
        this.numeroPredial = numeroPredial;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.cep = cep;
        this.contatoEmergencia = contatoEmergencia;
        this.dataEntrada = dataEntrada;
        this.rg = rg;
        this.ctpas = ctpas;
        this.email = email;
        this.senha = senha;
    }
}
