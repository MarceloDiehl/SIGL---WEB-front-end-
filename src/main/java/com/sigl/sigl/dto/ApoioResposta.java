package com.sigl.sigl.dto;

import com.sigl.sigl.model.apoio.OpcaoDeApoio;

import java.util.List;

/** Todas as listas de opções das telas numa resposta só (GET /api/apoio). */
public record ApoioResposta(
        List<Opcao> tipos,
        List<Opcao> condicoes,
        List<Opcao> cargos,
        List<Opcao> escolaridades,
        List<Opcao> generos,
        List<Opcao> metodosPagamento
) {

    public record Opcao(Integer id, String descricao) {

        public static List<Opcao> de(List<? extends OpcaoDeApoio> linhas) {
            return linhas.stream().map(o -> new Opcao(o.getId(), o.getDescricao())).toList();
        }
    }
}
