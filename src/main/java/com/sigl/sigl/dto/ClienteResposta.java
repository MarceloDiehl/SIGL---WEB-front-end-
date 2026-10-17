package com.sigl.sigl.dto;

import com.sigl.sigl.model.Cliente;
import com.sigl.sigl.seguranca.DadosPessoais;

import java.time.LocalDate;

/**
 * Cliente como a API devolve. O gerente recebe a ficha completa; os demais cargos, só o necessário
 * para atender (nome, contato, cidade e CPF mascarado). Os campos ausentes vão como null.
 */
public record ClienteResposta(
        Integer id,
        String nome,
        String sobrenome,
        LocalDate dataNascimento,
        String cpf,
        String rg,
        String endereco,
        Integer numeroPredial,
        String bairro,
        String cidade,
        String estado,
        String cep,
        String telefone,
        LocalDate clienteDesde
) {

    public static ClienteResposta completo(Cliente c) {
        return new ClienteResposta(c.getId(), c.getNome(), c.getSobrenome(), c.getDataNascimento(),
                c.getCpf(), c.getRg(), c.getEndereco(), c.getNumeroPredial(), c.getBairro(),
                c.getCidade(), c.getEstado(), c.getCep(), c.getTelefone(), c.getClienteDesde());
    }

    // Sem RG, nascimento e endereço; CPF mascarado
    public static ClienteResposta resumido(Cliente c) {
        return new ClienteResposta(c.getId(), c.getNome(), c.getSobrenome(), null,
                DadosPessoais.mascararCpf(c.getCpf()), null, null, null, null,
                c.getCidade(), c.getEstado(), null, c.getTelefone(), c.getClienteDesde());
    }
}
