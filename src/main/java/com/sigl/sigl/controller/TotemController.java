package com.sigl.sigl.controller;

import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.model.apoio.Cargo;
import com.sigl.sigl.repository.FuncionarioRepository;
import com.sigl.sigl.seguranca.UsuarioLogado;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Stream;

/**
 * Modo totem: a Consulta de preço fica travada em tela cheia para o cliente usar sozinho.
 * O estado fica na sessão (recarregar a página reabre o totem) e só é desfeito com a senha
 * de quem ativou (o usuário logado) ou de um gerente, conferida aqui no servidor.
 */
@RestController
@RequestMapping("/api/totem")
public class TotemController {

    public static final String ATRIBUTO_SESSAO = "totemAtivo";

    private final FuncionarioRepository funcionarioRepository;
    private final PasswordEncoder passwordEncoder;

    public TotemController(FuncionarioRepository funcionarioRepository, PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public record PedidoEncerrar(String senha) {
    }

    public static boolean ativo(HttpSession sessao) {
        return Boolean.TRUE.equals(sessao.getAttribute(ATRIBUTO_SESSAO));
    }

    @PostMapping("/ativar")
    public Map<String, Boolean> ativar(HttpSession sessao) {
        sessao.setAttribute(ATRIBUTO_SESSAO, true);
        return Map.of("ativo", true);
    }

    // Chamado periodicamente pelo totem.js: além de informar o estado, mantém a sessão viva
    @GetMapping("/status")
    public Map<String, Boolean> status(HttpSession sessao) {
        return Map.of("ativo", ativo(sessao));
    }

    @PostMapping("/encerrar")
    public Map<String, Object> encerrar(@RequestBody PedidoEncerrar pedido, HttpSession sessao,
                                        @AuthenticationPrincipal UsuarioLogado usuario) {
        if (pedido.senha() == null || pedido.senha().isBlank()) {
            return Map.of("encerrado", false, "mensagem", "Digite a senha.");
        }
        if (!senhaQueDestrava(pedido.senha(), usuario)) {
            return Map.of("encerrado", false, "mensagem", "Senha incorreta.");
        }
        sessao.removeAttribute(ATRIBUTO_SESSAO);
        return Map.of("encerrado", true);
    }

    // Com BCrypt cada hash tem seu próprio salt, então não dá para procurar "quem tem esta senha"
    // no banco: a senha digitada é conferida contra o hash de cada funcionário candidato.
    private boolean senhaQueDestrava(String senha, UsuarioLogado usuario) {
        Stream<Funcionario> candidatos = Stream.concat(
                funcionarioRepository.findById(usuario.getId()).stream(),
                funcionarioRepository.findByIdCargo(Cargo.GERENTE).stream());
        return candidatos.anyMatch(f -> passwordEncoder.matches(senha, f.getSenha()));
    }
}
