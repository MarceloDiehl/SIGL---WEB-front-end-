package com.sigl.sigl.service;

import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final ValidatorService validatorService;

    @Autowired
    public FuncionarioService(FuncionarioRepository funcionarioRepository, ValidatorService validatorService) {
        this.funcionarioRepository = funcionarioRepository;
        this.validatorService = validatorService;
    }

    public Funcionario salvar(Funcionario funcionario) {
        validarFuncionario(funcionario);
        if (funcionarioRepository.existsByCpf(funcionario.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado!");
        }
        if (funcionarioRepository.existsByEmail(funcionario.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado!");
        }

        return funcionarioRepository.save(funcionario);
    }

    private void validarFuncionario(Funcionario funcionario) {
        validatorService.validarCpf(funcionario.getCpf());
        validatorService.validarCep(funcionario.getCep());
        validatorService.validarTelefone(funcionario.getTelefone());
    }
}