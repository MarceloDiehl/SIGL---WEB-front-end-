package com.sigl.sigl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;

/** Dados do perfil "demo" carregados no H2 de teste, como num banco recém-criado. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
// banco H2 próprio: o histórico do Flyway com os dados de demonstração não vaza para os outros testes
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:sigl-demo;MODE=MySQL;DATABASE_TO_LOWER=TRUE;CASE_INSENSITIVE_IDENTIFIERS=TRUE;DB_CLOSE_DELAY=-1",
        "spring.flyway.locations=classpath:db/migration,classpath:db/h2,classpath:db/demo"
})
class DadosDeDemonstracaoTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private MockMvc mockMvc;

    private int contar(String tabela) {
        return jdbc.queryForObject("select count(*) from " + tabela, Integer.class);
    }

    @Test
    @DisplayName("Carrega uma conta de cada cargo, produtos de todos os tipos e clientes")
    void carregaDadosDeDemonstracao() {
        assertEquals(3, contar("cadastrofuncionario"));
        assertEquals(9, contar("cadastroproduto"));
        assertEquals(3, contar("cadastrocliente"));
        assertEquals(8, jdbc.queryForObject("select count(distinct idTipo) from cadastroproduto", Integer.class));
        assertEquals(3, jdbc.queryForObject("select count(distinct idCargo) from cadastrofuncionario", Integer.class));
    }

    @Test
    @DisplayName("Rodar o script de novo não duplica nada")
    void scriptPodeRodarDeNovo() throws Exception {
        try (Connection conexao = dataSource.getConnection()) {
            ScriptUtils.executeSqlScript(conexao, new ClassPathResource("db/demo/R__dados_de_demonstracao.sql"));
        }
        assertEquals(3, contar("cadastrofuncionario"));
        assertEquals(9, contar("cadastroproduto"));
        assertEquals(3, contar("cadastrocliente"));
    }

    @Test
    @DisplayName("Dá para entrar com a conta de gerente documentada no README")
    void entraComContaDeGerente() throws Exception {
        mockMvc.perform(formLogin("/login").user("login-email", "gerente@sigl.demo").password("login-senha", "gerente123"))
                .andExpect(authenticated().withRoles("GERENTE"));
    }
}
