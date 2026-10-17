package com.sigl.sigl.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "metodopagamento")
public class MetodoPagamento {

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

    public Integer getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }
}
