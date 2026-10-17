package com.sigl.sigl.repository;

import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.model.apoio.Cargo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** Grava de verdade no H2 criado pelas migrações (perfil "test"). */
@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)   // usa o H2 do perfil, não um banco genérico
class FuncionarioRepositoryTest {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Test
    @DisplayName("Grava telefones no formato exigido pelo cadastro: (51) 99812-4410, 15 caracteres")
    void salvar_TelefoneNoFormatoDoCadastro() {
        Funcionario f = new Funcionario();
        f.setNome("Ana");
        f.setSobrenome("Souza");
        f.setDataNascimento(LocalDate.of(1990, 1, 1));
        f.setIdCargo(Cargo.VENDEDOR);
        f.setIdEscolaridade(4);
        f.setIdGenero(1);
        f.setCpf("482.193.705-05");
        f.setRg("1234567890");
        f.setCtpas("1234567/001-0");
        f.setEndereco("Rua da República");
        f.setNumeroPredial(10);
        f.setBairro("Cidade Baixa");
        f.setCidade("Porto Alegre");
        f.setEstado("RS");
        f.setDataEntrada(LocalDate.now());
        f.setEmail("ana@sigl.demo");
        f.setSenha("{bcrypt}x");
        f.setTelefone("(51) 99812-4410");
        f.setContatoEmergencia("(51) 99777-1122");

        funcionarioRepository.saveAndFlush(f);   // sem a V6, o banco recusa: as colunas tinham 14 caracteres

        assertEquals("(51) 99812-4410", funcionarioRepository.findByEmail("ana@sigl.demo").orElseThrow().getTelefone());
    }
}
