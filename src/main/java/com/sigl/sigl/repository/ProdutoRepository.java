package com.sigl.sigl.repository;

import com.sigl.sigl.model.Produto;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProdutoRepository extends JpaRepository<Produto, String> {

    List<Produto> findByCodProdutoContainingIgnoreCase(String codProduto);

    Optional<Produto> findByCodProduto(String codProduto);

    // SELECT ... FOR UPDATE: a linha fica travada até o fim da transação, então dois caixas
    // vendendo o mesmo produto ao mesmo tempo não leem o mesmo estoque
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Produto p where p.codProduto = :codigo")
    Optional<Produto> buscarParaAtualizarEstoque(@Param("codigo") String codigo);
}
