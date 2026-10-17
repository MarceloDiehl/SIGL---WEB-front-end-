package com.sigl.sigl.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

// O usuário logado chega a todas as telas como ${usuario} (UsuarioNasTelas)
@Controller
public class ViewController {


    @GetMapping("/")
    public String showHomePage(HttpSession sessao, Model model) {
        // com o totem ativo na sessão, a página já abre travada nele (F5 não sai do totem)
        model.addAttribute("totemAtivo", TotemController.ativo(sessao));
        return "index";
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/venda")
    public String showVendaPage() {
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