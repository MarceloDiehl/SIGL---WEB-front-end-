package com.sigl.sigl.repository;

import com.sigl.sigl.model.ItemVenda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemVendaRepository extends JpaRepository<ItemVenda, Integer> {

    boolean existsByIdProduto(Integer idProduto);
}
