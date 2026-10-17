package com.sigl.sigl.controller;


import com.sigl.sigl.seguranca.UsuarioLogado;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {


    @GetMapping("/")
    public String showHomePage(HttpSession sessao, Model model, @AuthenticationPrincipal UsuarioLogado usuario) {
        // com o totem ativo na sessão, a página já abre travada nele (F5 não sai do totem)
        model.addAttribute("totemAtivo", TotemController.ativo(sessao));
        model.addAttribute("usuario", usuario);   // nome, cargo e iniciais no menu
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/venda")
    public String showVendaPage(Model model, @AuthenticationPrincipal UsuarioLogado usuario) {
        model.addAttribute("usuario", usuario);   // a venda é registrada em nome de quem está logado
        return "pages/venda";
    }


    @GetMapping("/pesquisa-produto")
    public String showPesquisaProdutoPage() {
        return "pages/pesquisa-produto";
    }

    @GetMapping("/pesquisa-membro")
    public String showComunidadePage() {
        return "pages/pesquisa-membro";
    }

    @GetMapping("/pesquisa-venda")
    public String showPesquisaVenda() {
        return "pages/pesquisa-venda";
    }

    @GetMapping("/pesquisa-avancada")
    public String showPesquisaAvancadaProduto() {
        return "pages/pesquisa-avancada";
    }

    @GetMapping("/cadastro-produto")
    public String showCadastrarProduto() {
        return "pages/cadastro-produto";
    }

    @GetMapping("/cadastro-membro")
    public String showCadastrarParceiro() {
        return "pages/cadastro-membro";
    }
}