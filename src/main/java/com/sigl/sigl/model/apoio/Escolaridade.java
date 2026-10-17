package com.sigl.sigl.model.apoio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

/** Escolaridade do funcionário. Somente leitura: as linhas vêm das migrações do Flyway. */
@Entity
@Immutable
@Table(name = "escolaridade")
public class Escolaridade implements OpcaoDeApoio {

    @Id
    private Integer id;

    @Column(name = "formacao", nullable = false, length = 45)
    private String descricao;

    protected Escolaridade() {
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
