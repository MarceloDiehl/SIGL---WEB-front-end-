package com.sigl.sigl.model.apoio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

/** Tipo de produto (Disco de Vinil, CD, Livros...). Somente leitura: as linhas vêm das migrações do Flyway. */
@Entity
@Immutable
@Table(name = "tipo")
public class Tipo implements OpcaoDeApoio {

    @Id
    private Integer id;

    @Column(name = "descricao", nullable = false, length = 45)
    private String descricao;

    protected Tipo() {
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
