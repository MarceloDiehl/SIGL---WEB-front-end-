package com.sigl.sigl.seguranca;

import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AuthorizeHttpRequestsConfigurer;

import java.util.Set;

/**
 * Matriz de permissões por cargo, num lugar só (item 10).
 *
 *                          Vendedor  Estoquista  Gerente
 *   Início, Consultar, Totem   ✔         ✔          ✔
 *   Estoque (ver)              ✔         ✔          ✔
 *   Cadastrar/editar/excluir
 *     produto                  –         ✔          ✔
 *   Nova venda, Histórico      ✔         –          ✔
 *   Cancelar venda             –         –          ✔
 *   Clientes (ver/cadastrar)   ✔         –          ✔
 *   Excluir cliente            –         –          ✔
 *   Funcionários (tudo)        –         –          ✔
 *
 * O servidor é quem garante as regras (aplicar); as telas usam as mesmas listas
 * (UsuarioLogado.isPode...) só para esconder o que o cargo não pode usar.
 */
public final class RegrasDeAcesso {

    public static final String VENDEDOR = "VENDEDOR";
    public static final String ESTOQUISTA = "ESTOQUISTA";
    public static final String GERENTE = "GERENTE";

    public static final Set<String> VENDER = Set.of(VENDEDOR, GERENTE);
    public static final Set<String> EDITAR_PRODUTOS = Set.of(ESTOQUISTA, GERENTE);
    public static final Set<String> ATENDER_CLIENTES = Set.of(VENDEDOR, GERENTE);
    // cancelar venda, excluir cliente e tudo de funcionários
    public static final Set<String> GERIR = Set.of(GERENTE);

    private RegrasDeAcesso() {
    }

    /** Regras de URL, da mais específica para a mais geral: vale a primeira que bater. */
    static void aplicar(AuthorizeHttpRequestsConfigurer<HttpSecurity>.AuthorizationManagerRequestMatcherRegistry regras) {
        regras
                // públicas
                .requestMatchers("/login", "/style/**", "/script/**", "/img/**", "/favicon.ico", "/error").permitAll()

                // vendas
                .requestMatchers(HttpMethod.POST, "/api/vendas/*/cancelar").hasAnyRole(papeis(GERIR))
                .requestMatchers("/venda", "/pesquisa-venda", "/api/vendas/**").hasAnyRole(papeis(VENDER))

                // produtos: consultar é de todos; cadastrar, editar e excluir não
                .requestMatchers(HttpMethod.GET, "/api/produtos/**").authenticated()
                .requestMatchers("/cadastro-produto", "/api/produtos/**").hasAnyRole(papeis(EDITAR_PRODUTOS))

                // pessoas
                .requestMatchers("/api/funcionarios/**").hasAnyRole(papeis(GERIR))
                .requestMatchers(HttpMethod.DELETE, "/api/clientes/**").hasAnyRole(papeis(GERIR))
                .requestMatchers("/pesquisa-membro", "/cadastro-membro", "/api/clientes/**").hasAnyRole(papeis(ATENDER_CLIENTES))

                // início, consultar produto, estoque (ver), totem e tabelas de apoio
                .anyRequest().authenticated();
    }

    private static String[] papeis(Set<String> quem) {
        return quem.toArray(String[]::new);
    }
}
