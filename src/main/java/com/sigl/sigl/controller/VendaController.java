package com.sigl.sigl.controller;

import com.sigl.sigl.model.Venda;
import com.sigl.sigl.repository.VendaRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@CrossOrigin(origins = "*")
public class VendaController {

    private final VendaRepository vendaRepository;

    public VendaController(VendaRepository vendaRepository) {
        this.vendaRepository = vendaRepository;
    }

    @GetMapping
    public List<Venda> listarVendas() {
        return vendaRepository.findAll();
    }

    @GetMapping("/busca")
    public List<Venda> buscarPorCodigo(@RequestParam String codigo) {
        return vendaRepository.findByCodProdutoContainingIgnoreCase(codigo);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Venda criarVenda(@Valid @RequestBody Venda venda) {
        return vendaRepository.save(venda);
    }
}
