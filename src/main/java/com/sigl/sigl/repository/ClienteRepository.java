package com.sigl.sigl.repository;

import com.sigl.sigl.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {
    boolean existsByCpf(String cpf);

    List<Cliente> findByCpfContaining(String cpf);
}