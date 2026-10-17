package com.sigl.sigl.model.apoio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

/** Condição do produto (novo ou usado). Somente leitura: as linhas vêm das migrações do Flyway. */
@Entity
@Immutable
@Table(name = "condicao")
public class Condicao implements OpcaoDeApoio {

    @Id
    private Integer id;

    @Column(name = "descricao", nullable = false, length = 45)
    private String descricao;

    protected Condicao() {
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getDescricao() {
        return descricao;
    }
}
