package com.sigl.sigl.repository;

import com.sigl.sigl.model.Funcionario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {

    boolean existsByCpf(String cpf);

    Optional<Funcionario> findByCpf(String cpf);

    Optional<Funcionario> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<Funcionario> findByEmailAndSenha(String email, String senha);

    boolean existsBySenha(String senha);

    Optional<Funcionario> findByRg(String rg);

    Optional<Funcionario> findByCtpas(String ctpas);

    List<Funcionario> findByCpfContaining(String cpf);
}