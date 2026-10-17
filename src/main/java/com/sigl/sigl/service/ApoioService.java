package com.sigl.sigl.service;

import com.sigl.sigl.dto.ApoioResposta;
import com.sigl.sigl.dto.ApoioResposta.Opcao;
import com.sigl.sigl.model.MetodoPagamento;
import com.sigl.sigl.model.apoio.Cargo;
import com.sigl.sigl.model.apoio.Condicao;
import com.sigl.sigl.model.apoio.Escolaridade;
import com.sigl.sigl.model.apoio.Genero;
import com.sigl.sigl.model.apoio.OpcaoDeApoio;
import com.sigl.sigl.model.apoio.Tipo;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/** Lê as tabelas de apoio; as telas montam selects e rótulos a partir daqui. */
@Service
public class ApoioService {

    private final EntityManager entityManager;

    public ApoioService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public ApoioResposta listarTudo() {
        return new ApoioResposta(
                Opcao.de(listar(Tipo.class)),
                Opcao.de(listar(Condicao.class)),
                Opcao.de(listar(Cargo.class)),
                Opcao.de(listar(Escolaridade.class)),
                Opcao.de(listar(Genero.class)),
                Opcao.de(listar(MetodoPagamento.class))
        );
    }

    // Uma consulta genérica serve para todas as tabelas, que têm o mesmo formato (id + texto)
    <T extends OpcaoDeApoio> List<T> listar(Class<T> tabela) {
        return entityManager
                .createQuery("select o from " + tabela.getSimpleName() + " o order by o.id", tabela)
                .getResultList();
    }
}
