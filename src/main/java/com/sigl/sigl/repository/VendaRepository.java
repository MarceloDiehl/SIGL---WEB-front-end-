package com.sigl.sigl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sigl.sigl.model.Venda;

import java.util.List;

@Repository
public interface VendaRepository extends JpaRepository<Venda, Integer> {
    List<Venda> findByCodProdutoContainingIgnoreCase(String codProduto);
}