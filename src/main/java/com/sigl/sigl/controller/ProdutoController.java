package com.sigl.sigl.controller;

import com.sigl.sigl.model.Produto;
import com.sigl.sigl.repository.ProdutoRepository;
import com.sigl.sigl.service.ProdutoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private ProdutoService produtoService;

    @GetMapping
    public List<Produto> listarProdutos() {
        return produtoRepository.findAll();
    }

    @GetMapping("/busca") // Busca parcial por código
    public List<Produto> buscarPorCodigo(@RequestParam String codigo) {
        return produtoRepository.findByCodProdutoContainingIgnoreCase(codigo);
    }

    @GetMapping("/codigo/{codigo}") // Busca exata por código
    public ResponseEntity<Produto> buscarPorCodigoExato(@PathVariable String codigo) {
        return produtoRepository.findByCodProduto(codigo)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Produto não encontrado com código: " + codigo));
    }

    @PostMapping
    public ResponseEntity<Object> cadastrarProduto(@Valid @RequestBody Produto produto) {
        try {
            // A anotação @Valid cuida das validações de formato (NotBlank, Size, etc)
            // Nossa lógica de negócio (código duplicado) está no service
            Produto produtoSalvo = produtoService.cadastrar(produto);
            return ResponseEntity.status(HttpStatus.CREATED).body(produtoSalvo);
        } catch (IllegalArgumentException e) {
            // Captura a exceção de negócio que criamos no service
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocorreu um erro inesperado.");
        }
    }

    @DeleteMapping("/{codigo}")
    public ResponseEntity<String> excluirProduto(@PathVariable String codigo) {
        return produtoRepository.findByCodProduto(codigo)
                .map(produto -> {
                    produtoRepository.delete(produto);
                    return ResponseEntity.ok("Produto código " + codigo + " excluído!");
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("Produto não encontrado para o código: " + codigo));
    }

    @PatchMapping("/{codProduto}/removerQuantidade")
    public ResponseEntity<Object> removerQuantidadeProduto(@PathVariable String codProduto, @RequestParam Integer quantidade) {
        try {
            Produto produtoAtualizado = produtoService.removerQuantidade(codProduto, quantidade);
            return ResponseEntity.ok(produtoAtualizado);
        } catch (IllegalArgumentException e) {
            // Captura tanto o erro de "produto não encontrado" quanto "estoque insuficiente"
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar quantidade.");
        }
    }

    @PutMapping("/{codProduto}")
    public ResponseEntity<?> atualizarProduto(
            @PathVariable String codProduto,
            @RequestBody Produto produtoAtualizado) {

        Optional<Produto> optProduto = produtoRepository.findByCodProduto(codProduto);
        if (!optProduto.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Produto não encontrado");
        }

        Produto produtoExistente = optProduto.get();

        if (produtoAtualizado.getQuantidade() != null) {
            produtoExistente.setQuantidade(produtoAtualizado.getQuantidade());
        }
        if (produtoAtualizado.getValor() != null) {
            produtoExistente.setValor(produtoAtualizado.getValor());
        }

        try {
            Produto salvo = produtoRepository.save(produtoExistente);
            return ResponseEntity.ok(salvo);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao atualizar o produto: " + e.getMessage());
        }
    }
}