package com.sigl.sigl.controller;

import com.sigl.sigl.seguranca.UsuarioLogado;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Coloca o usuário logado (${usuario}) no modelo de toda tela do ViewController: o menu mostra
 * nome e cargo, e cada tela esconde o que o cargo não pode usar. Na tela de login, fica nulo.
 */
@ControllerAdvice(assignableTypes = ViewController.class)
public class UsuarioNasTelas {

    @ModelAttribute("usuario")
    public UsuarioLogado usuario(@AuthenticationPrincipal UsuarioLogado usuario) {
        return usuario;
    }
}
