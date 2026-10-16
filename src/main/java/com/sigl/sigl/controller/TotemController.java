package com.sigl.sigl.controller;

import com.sigl.sigl.repository.FuncionarioRepository;
import com.sigl.sigl.service.HashSenha;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Modo totem: a Consulta de preço fica travada em tela cheia para o cliente usar sozinho.
 * O estado fica na sessão (recarregar a página reabre o totem) e só é desfeito com a senha
 * de um funcionário, conferida aqui no servidor.
 */
@RestController
@RequestMapping("/api/totem")
public class TotemController {

    public static final String ATRIBUTO_SESSAO = "totemAtivo";

    private final FuncionarioRepository funcionarioRepository;

    public TotemController(FuncionarioRepository funcionarioRepository) {
        this.funcionarioRepository = funcionarioRepository;
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
    public Map<String, Object> encerrar(@RequestBody PedidoEncerrar pedido, HttpSession sessao) {
        if (pedido.senha() == null || pedido.senha().isBlank()) {
            return Map.of("encerrado", false, "mensagem", "Digite a senha.");
        }
        if (!funcionarioRepository.existsBySenha(HashSenha.md5(pedido.senha()))) {
            return Map.of("encerrado", false, "mensagem", "Senha incorreta.");
        }
        sessao.removeAttribute(ATRIBUTO_SESSAO);
        return Map.of("encerrado", true);
    }
}
