package com.sigl.sigl.seguranca;

import com.sigl.sigl.controller.ViewController;
import com.sigl.sigl.model.apoio.Cargo;
import com.sigl.sigl.repository.FuncionarioRepository;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;

/**
 * Confere a matriz de RegrasDeAcesso rota a rota: 403 onde o cargo não pode; qualquer outra
 * resposta onde pode (as rotas de API sem controller carregado aqui respondem 404, o que mostra
 * que a segurança deixou passar).
 */
@WebMvcTest(ViewController.class)
@Import({SegurancaConfig.class, UsuarioService.class})
class RegrasDeAcessoTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FuncionarioRepository funcionarioRepository;

    private static UsuarioLogado usuario(int idCargo) {
        return new UsuarioLogado(1, "teste@orion.com.br", null, "Teste", "Orion", idCargo, "Cargo");
    }

    @ParameterizedTest(name = "{0} {1} {2} -> {3}")
    @CsvSource({
            // ---------- Vendedor ----------
            "1, GET,    /,                        pode",
            "1, GET,    /venda,                   pode",
            "1, GET,    /pesquisa-venda,          pode",
            "1, POST,   /api/vendas,              pode",
            "1, POST,   /api/vendas/1/cancelar,   negado",
            "1, GET,    /pesquisa-avancada,       pode",
            "1, GET,    /api/produtos,            pode",
            "1, GET,    /cadastro-produto,        negado",
            "1, POST,   /api/produtos,            negado",
            "1, DELETE, /api/produtos/132437,     negado",
            "1, GET,    /pesquisa-membro,         pode",
            "1, GET,    /cadastro-membro,         pode",
            "1, POST,   /api/clientes,            pode",
            "1, DELETE, /api/clientes/1,          negado",
            "1, GET,    /api/funcionarios,        negado",
            "1, POST,   /api/funcionarios,        negado",
            // ---------- Estoquista ----------
            "3, GET,    /venda,                   negado",
            "3, GET,    /pesquisa-venda,          negado",
            "3, GET,    /api/vendas,              negado",
            "3, POST,   /api/vendas,              negado",
            "3, GET,    /pesquisa-avancada,       pode",
            "3, GET,    /cadastro-produto,        pode",
            "3, POST,   /api/produtos,            pode",
            "3, PUT,    /api/produtos/132437,     pode",
            "3, DELETE, /api/produtos/132437,     pode",
            "3, GET,    /pesquisa-membro,         negado",
            "3, GET,    /api/clientes,            negado",
            "3, GET,    /api/funcionarios,        negado",
            "3, GET,    /api/totem/status,        pode",
            "3, GET,    /api/apoio,               pode",
            // ---------- Gerente ----------
            "2, GET,    /venda,                   pode",
            "2, POST,   /api/vendas/1/cancelar,   pode",
            "2, GET,    /cadastro-produto,        pode",
            "2, DELETE, /api/produtos/132437,     pode",
            "2, GET,    /cadastro-membro,         pode",
            "2, DELETE, /api/clientes/1,          pode",
            "2, GET,    /api/funcionarios,        pode",
            "2, DELETE, /api/funcionarios/1,      pode",
    })
    void matrizDePermissoes(int idCargo, String metodo, String caminho, String esperado) throws Exception {
        int status = mockMvc.perform(request(HttpMethod.valueOf(metodo), caminho)
                        .with(user(usuario(idCargo))).with(csrf()))
                .andReturn().getResponse().getStatus();

        if (esperado.equals("negado")) {
            assertEquals(403, status, "deveria ser negado");
        } else {
            assertNotEquals(403, status, "deveria ser permitido");
        }
    }

    @ParameterizedTest(name = "cargo {0}: papel {1}")
    @CsvSource({"1, VENDEDOR", "2, GERENTE", "3, ESTOQUISTA"})
    void papelPeloIdDoCargo(int idCargo, String papel) {
        assertEquals(papel, usuario(idCargo).getPapel());
        assertEquals(Cargo.GERENTE == idCargo, usuario(idCargo).isPodeGerir());
    }
}
