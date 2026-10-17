package com.sigl.sigl.dto;

import com.sigl.sigl.model.Funcionario;

import java.time.LocalDate;

/**
 * Funcionário como a API devolve: todos os dados da entidade, menos a senha.
 * A entidade Funcionario espelha a tabela; este record define o que sai no JSON.
 */
public record FuncionarioResposta(
        Integer id,
        String nome,
        String sobrenome,
        LocalDate dataNascimento,
        Integer idCargo,
        Integer idEscolaridade,
        String especialidade,
        String telefone,
        String cpf,
        Integer idGenero,
        String endereco,
        Integer numeroPredial,
        String bairro,
        String cidade,
        String estado,
        String cep,
        String contatoEmergencia,
        LocalDate dataEntrada,
        String rg,
        String ctpas,
        String email
) {

    public static FuncionarioResposta de(Funcionario f) {
        return new FuncionarioResposta(
                f.getId(), f.getNome(), f.getSobrenome(), f.getDataNascimento(),
                f.getIdCargo(), f.getIdEscolaridade(), f.getEspecialidade(), f.getTelefone(),
                f.getCpf(), f.getIdGenero(), f.getEndereco(), f.getNumeroPredial(),
                f.getBairro(), f.getCidade(), f.getEstado(), f.getCep(),
                f.getContatoEmergencia(), f.getDataEntrada(), f.getRg(), f.getCtpas(),
                f.getEmail()
        );
    }
}
