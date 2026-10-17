package com.sigl.sigl.service;

import com.sigl.sigl.model.Funcionario;
import com.sigl.sigl.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class FuncionarioService {

    private final FuncionarioRepository funcionarioRepository;
    private final ValidatorService validatorService;
    private final PasswordEncoder passwordEncoder;

    private static final int TAMANHO_MINIMO_SENHA = 5;

    @Autowired
    public FuncionarioService(FuncionarioRepository funcionarioRepository, ValidatorService validatorService,
                              PasswordEncoder passwordEncoder) {
        this.funcionarioRepository = funcionarioRepository;
        this.validatorService = validatorService;
        this.passwordEncoder = passwordEncoder;
    }

    public Funcionario salvar(Funcionario funcionario) {
        validarFuncionario(funcionario);
        if (funcionarioRepository.existsByCpf(funcionario.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado!");
        }
        if (funcionarioRepository.existsByEmail(funcionario.getEmail())) {
            throw new IllegalArgumentException("Email já cadastrado!");
        }

        // A senha chega como foi digitada e é gravada só como hash BCrypt ("{bcrypt}$2a$10$...")
        funcionario.setSenha(passwordEncoder.encode(funcionario.getSenha()));
        return funcionarioRepository.save(funcionario);
    }

    private void validarFuncionario(Funcionario funcionario) {
        validatorService.validarCpf(funcionario.getCpf());
        validatorService.validarCep(funcionario.getCep());
        validatorService.validarTelefone(funcionario.getTelefone());
        String senha = funcionario.getSenha();
        if (senha == null || senha.length() < TAMANHO_MINIMO_SENHA) {
            throw new IllegalArgumentException("A senha deve ter pelo menos " + TAMANHO_MINIMO_SENHA + " caracteres.");
        }
    }
}