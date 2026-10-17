package com.sigl.sigl.model;

import com.sigl.sigl.model.apoio.OpcaoDeApoio;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.Immutable;

@Entity
@Immutable
@Table(name = "metodopagamento")
public class MetodoPagamento implements OpcaoDeApoio {

    public static final int DINHEIRO = 1;

    @Id
    private Integer id;

    @Column(nullable = false, length = 55)
    private String descricao;

    protected MetodoPagamento() {
    }

    public MetodoPagamento(Integer id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public boolean isDinheiro() {
        return id != null && id == DINHEIRO;
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
