package com.sigl.sigl.model.apoio;

/**
 * Linha de uma tabela de apoio (tipo, condição, cargo...): um id e o texto mostrado nas telas.
 * As colunas do texto têm nomes diferentes em cada tabela; esta interface os unifica.
 */
public interface OpcaoDeApoio {

    Integer getId();

    String getDescricao();
}
