package com.sigl.sigl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sigl.sigl.model.Produto;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, String> {
    List<Produto> findByCodProdutoContainingIgnoreCase(String codProduto);
    Optional<Produto> findByCodProduto(String codProduto);
}