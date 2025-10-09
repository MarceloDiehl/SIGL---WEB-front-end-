package com.sigl.sigl.controller; // Ajuste o pacote conforme sua estrutura

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model; // Importe Model se for passar dados, por enquanto talvez não precise

@Controller // Anote a classe como um controlador Spring MVC
public class ViewController {


    @GetMapping("/") // Mapeia a URL raiz para index.html
    public String showHomePage() {
        return "index";
    }

    @GetMapping("/login") // Para login.html
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/venda") // Exemplo para a página de vendas
    public String showVendaPage() {
        return "pages/venda"; // Assumindo que venda.html está em templates/pages/
    }

    // Adicione mapeamentos para outras páginas se elas forem completas:
    @GetMapping("/pesquisa-produto")
    public String showPesquisaProdutoPage() {
        return "pages/pesquisa-produto"; // Assumindo que pesquisa-produto.html está em templates/pages/
    }

    @GetMapping("/pesquisa-membro")
    public String showComunidadePage() {
        return "pages/pesquisa-membro"; // Assumindo que comunidade.html está em templates/pages/
    }

    @GetMapping("/pesquisa-venda")
    public String showPesquisaVenda() {
        return "pages/pesquisa-venda"; // volta para login
    }

    @GetMapping("/pesquisa-avancada")
    public String showPesquisaAvancadaProduto() {
        return "pages/pesquisa-avancada"; // volta para login
    }

    @GetMapping("/cadastro-produto")
    public String showCadastrarProduto() {
        return "pages/cadastro-produto"; // volta para login
    }

    @GetMapping("/cadastro-membro")
    public String showCadastrarParceiro() {
        return "pages/cadastro-membro"; // volta para login
    }
}