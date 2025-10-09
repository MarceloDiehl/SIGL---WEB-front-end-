package com.sigl.sigl.service;

import com.sigl.sigl.model.Produto;
import com.sigl.sigl.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    @Autowired
    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    @Transactional
    public Produto cadastrar(Produto produto) {
        if (produtoRepository.existsById(produto.getCodProduto())) {
            throw new IllegalArgumentException("Erro: Já existe um produto com o código " + produto.getCodProduto());
        }
        return produtoRepository.save(produto);
    }

    @Transactional
    public Produto removerQuantidade(String codProduto, int quantidade) {
        if (quantidade <= 0) {
            throw new IllegalArgumentException("A quantidade a ser removida deve ser positiva.");
        }

        Produto produto = produtoRepository.findByCodProduto(codProduto)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado: " + codProduto));

        produto.removerQuantidade(quantidade);

        return produtoRepository.save(produto);
    }
}