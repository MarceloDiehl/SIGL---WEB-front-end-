package com.sigl.sigl.model.apoio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

/** Cargo do funcionário (Vendedor, Gerente, Estoquista). Somente leitura: as linhas vêm das migrações do Flyway. */
@Entity
@Immutable
@Table(name = "cargo")
public class Cargo implements OpcaoDeApoio {

    @Id
    private Integer id;

    @Column(name = "nomeCargo", nullable = false, length = 45)
    private String descricao;

    protected Cargo() {
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
